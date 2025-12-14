package cl.duoc.veterinaria

import android.app.Application
import cl.duoc.veterinaria.data.VeterinariaRepository

class VeterinariaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialización global del repositorio para asegurar que prefs nunca sea null
        VeterinariaRepository.init(this)
    }
}
