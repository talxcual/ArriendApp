package com.example.ui.screens.calendar

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ReservaEntity
import com.example.ui.theme.Fondo
import com.example.ui.theme.Primario
import com.example.ui.theme.Secundario
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToCatalog: () -> Unit,
    viewModel: CalendarViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val context = LocalContext.current
    var selectedReserva by remember { mutableStateOf<ReservaEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCatalog,
                containerColor = Secundario,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agendar")
            }
        },
        containerColor = Fondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Calendario de Reservas",
                style = MaterialTheme.typography.headlineSmall,
                color = Primario,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (reservas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay reservas programadas.", color = Color.Gray)
                }
            } else {
                reservas.forEach { reserva ->
                    val equipmentName = inventory.find { it.id == reserva.equipmentId }?.nombre ?: "Equipo ${reserva.equipmentId}"
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable { selectedReserva = reserva },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Secundario, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = reserva.fechaHora, color = Secundario, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text(
                                    text = reserva.estado,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(if (reserva.estado == "Entregado") Color(0xFF81C784) else Color(0xFFFFA000), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(text = equipmentName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primario)
                            Text(text = "Cliente: ${reserva.clienteNombre}", color = Color.DarkGray, fontSize = 14.sp)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val workerName by produceState("Cargando...", reserva.workerId) {
                                value = viewModel.getWorkerName(reserva.workerId)
                            }

                            Text(
                                text = "Agendado por: $workerName",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .background(Fondo, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedReserva != null) {
        val reserva = selectedReserva!!
        val equipmentName = inventory.find { it.id == reserva.equipmentId }?.nombre ?: "Equipo ${reserva.equipmentId}"
        val workerName by produceState("Cargando...", reserva.workerId) {
            value = viewModel.getWorkerName(reserva.workerId)
        }

        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { selectedReserva = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Detalles de Reserva",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primario,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Equipo: $equipmentName", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Secundario)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Estado: ${reserva.estado}", fontWeight = FontWeight.SemiBold, color = if (reserva.estado == "Entregado") Color(0xFF2E7D32) else Color(0xFFD84315))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fecha del Evento: ${reserva.fechaHora}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("Registrado el: ${reserva.fechaRegistro}", fontSize = 14.sp, color = Color.Gray)
                
                val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
                    maximumFractionDigits = 0
                }
                Text("Monto Total: ${clpFormatter.format(reserva.monto)}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Cliente: ${reserva.clienteNombre}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Dirección: ${reserva.clienteDireccion}")
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Agendado por: $workerName", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            if (reserva.clienteContacto.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${reserva.clienteContacto}"))
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No hay número registrado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primario),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Llamar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (reserva.clienteContacto.isNotEmpty()) {
                                val number = reserva.clienteContacto.replace("+", "").replace(" ", "")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$number"))
                                intent.setPackage("com.whatsapp")
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "No hay número registrado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Green
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("WhatsApp", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
