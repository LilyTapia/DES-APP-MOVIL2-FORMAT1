package cl.duoc.veterinaria

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import cl.duoc.veterinaria.data.VeterinariaRepository
import cl.duoc.veterinaria.service.NotificacionService
import cl.duoc.veterinaria.ui.navigation.NavGraph
import cl.duoc.veterinaria.ui.theme.VeterinariaAppTheme

/**
 * MainActivity es la actividad principal y el punto de entrada de la aplicación.
 * Configura la interfaz de usuario utilizando Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    // Registro para solicitar permisos en tiempo de ejecución
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido
            iniciarServicioNotificaciones()
        } else {
            // Permiso denegado
        }
    }

    /**
     * onCreate se llama cuando la actividad es creada por primera vez.
     * Aquí se inicializa la interfaz de usuario de la aplicación.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Repositorio con Contexto (para SharedPreferences)
        VeterinariaRepository.init(applicationContext)

        // Solicitar permisos de notificación al iniciar (Android 13+)
        // Y si ya tiene permiso, iniciar el servicio automáticamente
        verificarYPedirPermisos()

        // Habilita el modo de borde a borde para que la app ocupe toda la pantalla
        enableEdgeToEdge()
        // setContent define el diseño de la actividad utilizando funciones de Composable.
        setContent {
            // VeterinariaAppTheme aplica el tema personalizado de la aplicación (colores, fuentes, etc.)
            VeterinariaAppTheme {
                // Scaffold es un layout predefinido de Material Design que proporciona una estructura básica.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Contenedor principal para aplicar el padding del sistema al contenido
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph()
                    }
                }
            }
        }
    }

    private fun verificarYPedirPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Ya tiene permiso, iniciamos el servicio
                    iniciarServicioNotificaciones()
                }
                else -> {
                    // No tiene permiso, lo pedimos
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Para versiones anteriores a Android 13, no se requiere permiso en runtime para notificaciones
            // Iniciamos el servicio directamente
            iniciarServicioNotificaciones()
        }
    }

    private fun iniciarServicioNotificaciones() {
        val intent = Intent(this, NotificacionService::class.java)
        startService(intent)
    }
}
