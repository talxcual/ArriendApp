package com.kleber.arriendapp.ui.screens.logistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kleber.arriendapp.data.ReservaEntity
import com.kleber.arriendapp.ui.theme.Fondo
import com.kleber.arriendapp.ui.theme.Primario
import com.kleber.arriendapp.ui.theme.Secundario

@Composable
fun DashboardScreen(
    onNavigateToEntrega: (String) -> Unit,
    onNavigateToRetiro: (String) -> Unit,
    viewModel: LogisticsViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val inventory by viewModel.inventory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoStatCard(
                title = "Entregas Hoy",
                stat = "${reservas.count { it.estado == "Pendiente" || it.estado == "En Camino" }}",
                icon = Icons.Default.LocationOn,
                badge = "Hoy",
                modifier = Modifier.weight(1f)
            )
            BentoStatCard(
                title = "Retiros Hoy",
                stat = "${reservas.count { it.estado == "Entregado" }}",
                icon = Icons.Default.Refresh,
                badge = "Pendiente",
                modifier = Modifier.weight(1f)
            )
            BentoStatCard(
                title = "Ingresos",
                stat = "$${reservas.sumOf { it.monto }.toInt()}",
                icon = Icons.Default.ShoppingCart,
                badge = "Est.",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ruta del Día",
            style = MaterialTheme.typography.titleLarge,
            color = Primario,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reservas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay entregas o retiros programados.", color = Color.Gray)
            }
        } else {
            reservas.forEachIndexed { index, routeItem ->
                val eqName = inventory.find { it.id == routeItem.equipmentId }?.nombre ?: "Equipo ${routeItem.equipmentId}"
                val routeType = if (routeItem.estado == "Entregado") "RETIRO" else "ENTREGA"

                TimelineItemRow(
                    reserva = routeItem,
                    equipmentName = eqName,
                    type = routeType,
                    isLast = index == reservas.lastIndex,
                    onClickCard = {
                        if (routeType == "ENTREGA") {
                            onNavigateToEntrega(routeItem.id)
                        } else {
                            onNavigateToRetiro(routeItem.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BentoStatCard(title: String, stat: String, icon: ImageVector, badge: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(icon, contentDescription = null, tint = Secundario, modifier = Modifier.size(20.dp))
                Text(text = badge, fontSize = 10.sp, color = Primario, modifier = Modifier.background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stat, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primario)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun TimelineItemRow(reserva: ReservaEntity, equipmentName: String, type: String, isLast: Boolean, onClickCard: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(
                modifier = Modifier.size(16.dp).background(if (type == "ENTREGA") Secundario else Color(0xFFFFA000), CircleShape)
            )
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { onClickCard() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = type, color = if (type == "ENTREGA") Secundario else Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = reserva.fechaHora, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = reserva.clienteNombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primario)
                Text(text = equipmentName, color = Color.DarkGray, fontSize = 14.sp)
                Text(text = reserva.clienteDireccion, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
