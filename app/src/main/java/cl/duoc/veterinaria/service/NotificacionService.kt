package cl.duoc.veterinaria.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.model.TipoServicio

class NotificacionService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tipoAtencion = intent?.getStringExtra("EXTRA_TIPO_ATENCION")

        // --- Lógica Mejorada ---
        val tituloPersonalizado = intent?.getStringExtra("EXTRA_TITULO")
        val textoPersonalizado = intent?.getStringExtra("EXTRA_TEXTO")

        val (titulo, texto) = if (tituloPersonalizado != null && textoPersonalizado != null) {
            // Usar título y texto personalizados si se proveen
            tituloPersonalizado to textoPersonalizado
        } else {
            // Lógica original como fallback
            when (tipoAtencion) {
                TipoServicio.CONTROL.name -> "Control Agendado" to "Se ha agendado un nuevo control."
                TipoServicio.VACUNA.name -> "Vacunación Agendada" to "Se ha agendado una nueva vacunación."
                TipoServicio.URGENCIA.name -> "Urgencia Registrada" to "Se ha registrado una nueva urgencia."
                else -> "Atención Veterinaria" to "Se ha registrado una nueva atención."
            }
        }

        crearNotificacion(titulo, texto)
        return START_NOT_STICKY
    }

    private fun crearNotificacion(titulo: String, texto: String) {
        val canalId = "veterinaria_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(canalId, "Notificaciones Veterinaria", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(this, canalId)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        // Usar un ID aleatorio para que las notificaciones no se reemplacen entre sí
        manager.notify((1..1000).random(), notificacion)
    }
}
