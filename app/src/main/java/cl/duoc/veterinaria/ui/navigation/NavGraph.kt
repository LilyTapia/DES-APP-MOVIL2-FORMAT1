package cl.duoc.veterinaria.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.veterinaria.ui.auth.LoginScreen
import cl.duoc.veterinaria.ui.registro.DuenoScreen
import cl.duoc.veterinaria.ui.registro.MascotaScreen
import cl.duoc.veterinaria.ui.registro.ResumenScreen
import cl.duoc.veterinaria.ui.registro.ServicioScreen
import cl.duoc.veterinaria.ui.screens.BienvenidaScreen
import cl.duoc.veterinaria.ui.screens.ListadoScreen
import cl.duoc.veterinaria.ui.screens.PedidoScreen
import cl.duoc.veterinaria.ui.viewmodel.ConsultaViewModel
import cl.duoc.veterinaria.ui.viewmodel.MainViewModel
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val registroViewModel: RegistroViewModel = viewModel()
    val (isLoggedIn, setLoggedIn) = remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { setLoggedIn(true) })
    } else {
        NavHost(
            navController = navController,
            startDestination = "bienvenida_screen",
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable("bienvenida_screen") {
                val mainViewModel: MainViewModel = viewModel()
                BienvenidaScreen(
                    onNavigateToNext = { navController.navigate("dueno_screen") },
                    onNavigateToRegistro = { navController.navigate("dueno_screen") },
                    onNavigateToListado = { navController.navigate("listado_screen") },
                    onNavigateToPedidos = { navController.navigate("pedidos_screen") },
                    viewModel = mainViewModel
                )
            }
            composable("dueno_screen") {
                DuenoScreen(viewModel = registroViewModel, onNextClicked = {
                    navController.navigate("mascota_screen")
                })
            }
            composable("mascota_screen") {
                MascotaScreen(viewModel = registroViewModel, onNextClicked = {
                    navController.navigate("servicio_screen")
                })
            }
            composable("servicio_screen") {
                ServicioScreen(viewModel = registroViewModel, onNextClicked = {
                    navController.navigate("pedidos_screen")
                })
            }
            composable("pedidos_screen") {
                PedidoScreen(
                    viewModel = registroViewModel,
                    onNextClicked = { navController.navigate("resumen_screen") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("resumen_screen") {
                ResumenScreen(viewModel = registroViewModel, onConfirmClicked = {
                    registroViewModel.clearData()
                    navController.popBackStack("bienvenida_screen", inclusive = true)
                    navController.navigate("bienvenida_screen")
                })
            }
            composable("listado_screen") {
                val consultaViewModel: ConsultaViewModel = viewModel()
                ListadoScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = consultaViewModel
                )
            }
        }
    }
}
