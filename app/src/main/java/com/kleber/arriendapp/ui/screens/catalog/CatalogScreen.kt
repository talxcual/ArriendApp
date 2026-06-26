package com.kleber.arriendapp.ui.screens.catalog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kleber.arriendapp.data.InventoryEntity
import com.kleber.arriendapp.ui.theme.Fondo
import com.kleber.arriendapp.ui.theme.Primario
import com.kleber.arriendapp.ui.theme.Secundario
import java.text.NumberFormat
import java.util.Locale
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = viewModel()
) {
    val context = LocalContext.current
    val inventory by viewModel.inventory.collectAsState()
    var selectedItemForRental by remember { mutableStateOf<InventoryEntity?>(null) }
    var argSearchQuery by remember { mutableStateOf("") }
    
    var showDeleteAlertFor by remember { mutableStateOf<InventoryEntity?>(null) }
    var itemToEdit by remember { mutableStateOf<InventoryEntity?>(null) }
    var isFormOpen by remember { mutableStateOf(false) }

    val filteredItems = inventory.filter {
        it.nombre.contains(argSearchQuery, ignoreCase = true)
    }

    if (showDeleteAlertFor != null) {
        AlertDialog(
            onDismissRequest = { showDeleteAlertFor = null },
            title = { Text("Confirmar Eliminación", color = Primario, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar este equipo del inventario?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(showDeleteAlertFor!!.id)
                    showDeleteAlertFor = null
                }) { Text("Sí", color = Secundario, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAlertFor = null }) { Text("No", color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

    if (isFormOpen) {
        var formNombre by remember { mutableStateOf(itemToEdit?.nombre ?: "") }
        var formPrecio by remember { mutableStateOf(if (itemToEdit != null) itemToEdit!!.precio.toInt().toString() else "") }
        var formImg by remember { mutableStateOf(itemToEdit?.imagen ?: "") }

        ModalBottomSheet(
            onDismissRequest = { isFormOpen = false; itemToEdit = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (itemToEdit != null) "Modificar Equipo" else "Nuevo Equipo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primario,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = formNombre,
                    onValueChange = { formNombre = it },
                    label = { Text("Nombre del equipo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = formPrecio,
                    onValueChange = { formPrecio = it },
                    label = { Text("Precio de arriendo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = formImg,
                    onValueChange = { formImg = it },
                    label = { Text("URL de Foto (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (formNombre.isNotEmpty() && formPrecio.isNotEmpty()) {
                            viewModel.saveItem(itemToEdit?.id, formNombre, formPrecio.toDoubleOrNull() ?: 0.0, formImg)
                            isFormOpen = false
                            itemToEdit = null
                        } else {
                            Toast.makeText(context, "Completa nombre y precio", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primario),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = argSearchQuery,
                onValueChange = { argSearchQuery = it },
                placeholder = { Text("Buscar equipos...") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Secundario,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { isFormOpen = true },
                modifier = Modifier
                    .size(52.dp)
                    .background(Secundario, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Equipo", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector horizontal de fechas (simulado)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DateScrollWidget(day = "HOY", date = "24", select = true)
            DateScrollWidget(day = "LUN", date = "25")
            DateScrollWidget(day = "MAR", date = "26")
            DateScrollWidget(day = "MIÉ", date = "27")
            DateScrollWidget(day = "JUE", date = "28")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron equipos.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems) { item ->
                    InventoryGridCard(
                        item = item,
                        onClick = {
                            if (item.estado == "DISPONIBLE") {
                                selectedItemForRental = item
                            } else {
                                Toast.makeText(context, "Este equipo ya está Ocupado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onEdit = {
                            itemToEdit = item
                            isFormOpen = true
                        },
                        onDelete = {
                            showDeleteAlertFor = item
                        }
                    )
                }
            }
        }
    }

    if (selectedItemForRental != null) {
        val item = selectedItemForRental!!
        var clienteNombre by remember { mutableStateOf("") }
        var clienteDireccion by remember { mutableStateOf("") }
        var clienteContacto by remember { mutableStateOf("") }
        var eventDate by remember { mutableStateOf("") }
        var eventTime by remember { mutableStateOf("") }

        val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0
        }

        ModalBottomSheet(
            onDismissRequest = { selectedItemForRental = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Confirmar Nuevo Arriendo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primario,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Equipo: ${item.nombre} (${clpFormatter.format(item.precio)})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Secundario,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = clienteNombre,
                    onValueChange = { clienteNombre = it },
                    label = { Text("Nombre del Cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = clienteDireccion,
                    onValueChange = { clienteDireccion = it },
                    label = { Text("Dirección de Entrega") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = clienteContacto,
                    onValueChange = { clienteContacto = it },
                    label = { Text("Teléfono de Contacto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d -> eventDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y) },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (eventDate.isEmpty()) "Fecha" else eventDate, color = Primario)
                    }

                    OutlinedButton(
                        onClick = {
                            val c = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, h, m -> eventTime = String.format(Locale.getDefault(), "%02d:%02d", h, m) },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (eventTime.isEmpty()) "Hora" else eventTime, color = Primario)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (clienteNombre.isEmpty() || clienteDireccion.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty()) {
                            Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        viewModel.scheduleRental(item, clienteNombre, clienteDireccion, clienteContacto, eventDate, eventTime)
                        Toast.makeText(context, "¡Arriendo Agendado!", Toast.LENGTH_SHORT).show()
                        selectedItemForRental = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primario),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar Arriendo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = { selectedItemForRental = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Regresar", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DateScrollWidget(day: String, date: String, select: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(64.dp, 80.dp)
            .background(
                color = if (select) Secundario.copy(alpha = 0.2f) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(text = day, fontSize = 12.sp, color = if (select) Secundario else Color.Gray)
        Text(text = date, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Primario)
    }
}

@Composable
fun InventoryGridCard(
    item: InventoryEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = item.imagen.ifEmpty { "https://via.placeholder.com/300" },
                    contentDescription = item.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                val bgColor = if (item.estado == "DISPONIBLE") Color(0xFFC8E6C9) else Color(0xFFFFCCBC)
                val txtColor = if (item.estado == "DISPONIBLE") Color(0xFF2E7D32) else Color(0xFFD84315)
                Text(
                    text = item.estado,
                    color = txtColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(bgColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                var menuExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Primario, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Modificar", color = Primario) },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primario
                )
                val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
                    maximumFractionDigits = 0
                }
                Text(
                    text = clpFormatter.format(item.precio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secundario,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
