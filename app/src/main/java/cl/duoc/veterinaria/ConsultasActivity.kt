package cl.duoc.veterinaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import cl.duoc.veterinaria.ui.screens.ListadoScreen
import cl.duoc.veterinaria.ui.theme.VeterinariaAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.ui.viewmodel.ConsultaViewModel

/**
 * Actividad secundaria dedicada exclusivamente a la gestión y listado de consultas.
 * Se lanza mediante un Intent explícito desde la MainActivity.
 */
class ConsultasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VeterinariaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Reutilizamos la pantalla de listado existente
                    val consultaViewModel: ConsultaViewModel = viewModel()
                    ListadoScreen(
                        onBack = { finish() }, // Al volver, cerramos esta actividad para regresar a la anterior
                        viewModel = consultaViewModel,
                        // Aplicamos el padding que nos da el Scaffold para evitar solapamientos
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) 
                    )
                }
            }
        }
    }
}
