package cl.duoc.veterinaria.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast

/**
 * BroadcastReceiver que escucha cambios en la conectividad del dispositivo.
 * Muestra un mensaje al usuario cuando cambia el estado de la red.
 */
class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

            val mensaje = if (isConnected) {
                "Conexión establecida. Sincronizando datos..."
            } else {
                "Sin conexión a Internet. Trabajando en modo local."
            }

            Log.d("ConnectivityReceiver", mensaje)
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}
