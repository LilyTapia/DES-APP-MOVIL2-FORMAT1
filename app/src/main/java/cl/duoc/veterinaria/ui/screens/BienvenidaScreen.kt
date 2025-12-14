package cl.duoc.veterinaria.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.ConsultasActivity
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.service.NotificacionService
import cl.duoc.veterinaria.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienvenidaScreen(
    onNavigateToNext: () -> Unit,
    onNavigateToRegistro: () -> Unit,
    onNavigateToListado: () -> Unit,
    onNavigateToPedidos: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Observamos el estado DESDE EL VIEWMODEL (no desde el repositorio directo)
    val totalMascotas by viewModel.totalMascotas.collectAsState()
    val totalConsultas by viewModel.totalConsultas.collectAsState()
    val ultimoDueno by viewModel.ultimoDueno.collectAsState()

    // Estado para el DropdownMenu (Menú de 3 puntos)
    var showMenu by remember { mutableStateOf(false) }
    
    // Estado para el Navigation Drawer (Menú Lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Estado para controlar la animación de entrada
    var isVisible by remember { mutableStateOf(false) }

    // Activamos la animación al iniciar
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Estructura del Menú Lateral (Drawer)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Veterinaria App",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                // Opción 1: Inicio
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Opción 2: Nuevo Registro
                NavigationDrawerItem(
                    label = { Text("Nuevo Registro") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToRegistro() 
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Opción 3: Listado de Mascotas (Intent Explícito a Nueva Activity)
                NavigationDrawerItem(
                    label = { Text("Listado (Nueva Activity)") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        // Lanzamos la activity secundaria con Intent Explícito
                        val intent = Intent(context, ConsultasActivity::class.java)
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Opción 4: Pedidos
                NavigationDrawerItem(
                    label = { Text("Pedidos (Demo)") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToPedidos() 
                    },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Opción 5: Recordar Última Alerta (Antes: Iniciar Servicio)
                NavigationDrawerItem(
                    label = { Text("Ver Última Alerta") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Lanzamos el servicio sin extra para que consulte SharedPreferences
                        // y muestre la última notificación válida.
                        val intent = Intent(context, NotificacionService::class.java)
                        context.startService(intent)
                    },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        // Contenido Principal (Scaffold)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("VeterinariaApp") },
                    navigationIcon = {
                        // Botón Hamburguesa para abrir el Drawer
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                        }
                    },
                    actions = {
                        // Mantenemos el DropdownMenu original también (Menú de opciones)
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Registro") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToRegistro()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            )
                            // Navegación original por Compose (mantenida por compatibilidad)
                            DropdownMenuItem(
                                text = { Text("Listado (Compose)") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToListado()
                                },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                                }
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Usamos Box para apilar capas (Fondo + Contenido)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // --- CAPA 1: FONDO ---
                Image(
                    painter = painterResource(id = R.drawable.fondo1),
                    contentDescription = null, 
                    modifier = Modifier.fillMaxSize(), 
                    contentScale = ContentScale.Crop, 
                    alpha = 0.1f
                )

                // --- CAPA 2: CONTENIDO ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animación de entrada
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { -40 })
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Bienvenido a VeterinariaApp",
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            // Imagen central (Logo)
                            Image(
                                painter = painterResource(id = R.drawable.inicio),
                                contentDescription = "Logo de la veterinaria",
                                modifier = Modifier.size(200.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Tarjeta de Resumen
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Resumen del Sistema",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Mascotas Registradas: $totalMascotas",
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Consultas Realizadas: $totalConsultas",
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Último Dueño: $ultimoDueno",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(onClick = onNavigateToNext) {
                                Text("Iniciar Registro")
                            }
                        }
                    }
                }
            }
        }
    }
}
