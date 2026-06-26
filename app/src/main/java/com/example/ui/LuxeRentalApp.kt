package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.LuxeRentalDao
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.catalog.CatalogScreen
import com.example.ui.screens.logistics.DashboardScreen
import com.example.ui.screens.logistics.DeliveryScreen
import com.example.ui.screens.logistics.PickupScreen
import com.example.ui.theme.Primario
import com.example.ui.theme.Secundario
import kotlinx.coroutines.launch

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxeRentalApp(dao: LuxeRentalDao) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isChatOpen by remember { mutableStateOf(false) }
    var fabOffset by remember { mutableStateOf(IntOffset(0, 0)) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != null && currentRoute != "login" && !currentRoute.startsWith("entrega") && !currentRoute.startsWith("retiro")) {
                TopAppBar(
                    title = {
                        Text(
                            when (currentRoute) {
                                "catalogo" -> "Catálogo"
                                "logistica" -> "Logística"
                                "calendario" -> "Calendario"
                                else -> "LuxeRental Pro"
                            },
                            color = Primario
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                // Simulador de exportación
                                Toast.makeText(context, "Exportando a Google Sheets en segundo plano...", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Exportar a Sheets", tint = Primario)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != null && currentRoute != "login" && !currentRoute.startsWith("entrega") && !currentRoute.startsWith("retiro")) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "catalogo",
                        onClick = { navController.navigate("catalogo") { popUpTo("catalogo") { inclusive = true } } },
                        label = { Text("Catálogo") },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Catálogo") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "calendario",
                        onClick = { navController.navigate("calendario") },
                        label = { Text("Calendario") },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "logistica",
                        onClick = { navController.navigate("logistica") },
                        label = { Text("Logística") },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Logística") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("catalogo") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("catalogo") {
                    CatalogScreen()
                }

                composable("logistica") {
                    DashboardScreen(
                        onNavigateToEntrega = { id -> navController.navigate("entrega/$id") },
                        onNavigateToRetiro = { id -> navController.navigate("retiro/$id") }
                    )
                }

                composable(
                    route = "entrega/{reservaId}",
                    arguments = listOf(navArgument("reservaId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("reservaId") ?: ""
                    DeliveryScreen(
                        reservaId = id,
                        onBackPressed = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "retiro/{reservaId}",
                    arguments = listOf(navArgument("reservaId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("reservaId") ?: ""
                    PickupScreen(
                        reservaId = id,
                        onBackPressed = { navController.popBackStack() }
                    )
                }

                composable("calendario") {
                    CalendarScreen(
                        onNavigateToCatalog = { navController.navigate("catalogo") }
                    )
                }
            }

            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != null && currentRoute != "login") {
                FloatingActionButton(
                    onClick = { isChatOpen = true },
                    containerColor = Secundario,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomEnd)
                        .padding(16.dp)
                        .offset { fabOffset }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                fabOffset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                            }
                        }
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "Asistente AI",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (isChatOpen) {
                com.example.ui.screens.ai.GeminiChatOverlay(
                    onDismiss = { isChatOpen = false }
                )
            }
        }
    }
}
