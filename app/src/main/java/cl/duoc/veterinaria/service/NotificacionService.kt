package cl.duoc.veterinaria.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cl.duoc.veterinaria.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificacionService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var isRunning = false
    
    private val CHANNEL_ID = "recordatorios_veterinaria"
    private val NOTIFICATION_ID_FOREGROUND = 1
    private val NOTIFICATION_ID_ALERTA = 2 
    
    private var prefs: SharedPreferences? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Inicializar preferencias
        prefs = getSharedPreferences("veterinaria_prefs", Context.MODE_PRIVATE)
        
        crearCanalNotificaciones()

        val notificacionPersistente = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Servicio Veterinario Activo")
            .setContentText("Monitoreando salud de mascotas...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID_FOREGROUND, notificacionPersistente)
        
        // Verificar si se recibe un tipo de atención específico en el Intent
        val tipoExplícito = intent?.getStringExtra("EXTRA_TIPO_ATENCION")
        
        if (tipoExplícito != null) {
            notificarTipo(tipoExplícito)
        } else {
            // Si no, intentar recuperar el último guardado
            serviceScope.launch {
                verificarYNotificarDesdePrefs()
            }
        }

        if (!isRunning) {
            isRunning = true
            iniciarRecordatorios()
        }

        return START_STICKY
    }

    private fun iniciarRecordatorios() {
        serviceScope.launch {
            while (isRunning) {
                try {
                    delay(3000) // Intervalo de verificación
                    
                    val notifico = verificarYNotificarDesdePrefs()
                    
                    if (notifico) {
                        // Esperar antes de la siguiente notificación
                        delay(10000)
                    }
                } catch (e: Exception) {
                    Log.e("NotificacionService", "Error en el ciclo de notificaciones", e)
                }
            }
        }
    }

    /**
     * Verifica SharedPreferences y notifica si existe un tipo de atención.
     */
    private fun verificarYNotificarDesdePrefs(): Boolean {
        val tipoAtencionCodigo = prefs?.getString("ULTIMO_TIPO", null)
        if (tipoAtencionCodigo != null) {
            notificarTipo(tipoAtencionCodigo)
            return true
        }
        return false
    }

    /**
     * Muestra la notificación según el código del servicio.
     */
    private fun notificarTipo(tipoCodigo: String) {
        val (nombreAmigable, mensajePersonalizado) = when {
            tipoCodigo.equals("VACUNA", ignoreCase = true) -> 
                Pair("Vacunación", "¡Recuerda agendar la próxima dosis en 1 año!")
            
            tipoCodigo.equals("CIRUGIA", ignoreCase = true) -> 
                Pair("Cirugía Menor", "Revisa la cicatrización y medicación.")
            
            tipoCodigo.equals("URGENCIA", ignoreCase = true) -> 
                Pair("Urgencia", "Observación constante por 24hrs.")
            
            tipoCodigo.equals("CONTROL", ignoreCase = true) -> 
                Pair("Control Sano", "Próximo control sano en 6 meses.")
            
            else -> Pair(tipoCodigo, "Mantén al día los cuidados.")
        }

        enviarNotificacion(
            titulo = "🩺 Seguimiento: $nombreAmigable",
            mensaje = mensajePersonalizado
        )
    }

    private fun enviarNotificacion(titulo: String, mensaje: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALERTA, notificacion)
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Recordatorios de Salud", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Notificaciones de seguimiento veterinario"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceJob.cancel()
    }
}
