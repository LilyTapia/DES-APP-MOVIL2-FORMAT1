package cl.duoc.veterinaria.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Define el contrato para el repositorio de la veterinaria.
 */
interface IVeterinariaRepository {
    val totalMascotasRegistradas: StateFlow<Int>
    val totalConsultasRealizadas: StateFlow<Int>
    val nombreUltimoDueno: StateFlow<String>
    val listaMascotas: StateFlow<List<String>>
    val ultimaAtencionTipo: StateFlow<String?>

    fun registrarAtencion(nombreDueno: String, cantidadMascotas: Int, nombreMascota: String, especieMascota: String, tipoServicio: String? = null)
    
    // Inicializar el contexto para SharedPreferences
    fun init(context: Context)

    fun eliminarMascota(nombreMascota: String)
    fun editarMascota(textoOriginal: String, textoNuevo: String)
}

/**
 * Implementación Singleton del repositorio que guarda los datos de la app en memoria y SharedPreferences.
 */
object VeterinariaRepository : IVeterinariaRepository {
    private val _totalMascotasRegistradas = MutableStateFlow(0)
    private val _totalConsultasRealizadas = MutableStateFlow(0)
    private val _nombreUltimoDueno = MutableStateFlow("N/A")
    private val _listaMascotas = MutableStateFlow<List<String>>(emptyList())
    private val _ultimaAtencionTipo = MutableStateFlow<String?>(null)
    
    private var prefs: SharedPreferences? = null

    override val totalMascotasRegistradas: StateFlow<Int> = _totalMascotasRegistradas.asStateFlow()
    override val totalConsultasRealizadas: StateFlow<Int> = _totalConsultasRealizadas.asStateFlow()
    override val nombreUltimoDueno: StateFlow<String> = _nombreUltimoDueno.asStateFlow()
    override val listaMascotas: StateFlow<List<String>> = _listaMascotas.asStateFlow()
    override val ultimaAtencionTipo: StateFlow<String?> = _ultimaAtencionTipo.asStateFlow()

    override fun init(context: Context) {
        prefs = context.getSharedPreferences("veterinaria_prefs", Context.MODE_PRIVATE)
        // Recuperar último tipo guardado si existe
        val ultimoGuardado = prefs?.getString("ULTIMO_TIPO", null)
        if (ultimoGuardado != null) {
            _ultimaAtencionTipo.value = ultimoGuardado
        }
    }

    override fun registrarAtencion(nombreDueno: String, cantidadMascotas: Int, nombreMascota: String, especieMascota: String, tipoServicio: String?) {
        _nombreUltimoDueno.value = nombreDueno
        _totalMascotasRegistradas.update { it + cantidadMascotas }
        _totalConsultasRealizadas.update { it + 1 }
        
        if (tipoServicio != null) {
            _ultimaAtencionTipo.value = tipoServicio
            // Guardar el tipo de servicio en SharedPreferences
            prefs?.edit()?.putString("ULTIMO_TIPO", tipoServicio)?.commit()
        }

        val nuevaEntrada = "Mascota: $nombreMascota ($especieMascota) - Dueño: $nombreDueno"
        _listaMascotas.update { it + nuevaEntrada }
    }

    override fun eliminarMascota(nombreMascota: String) {
        _listaMascotas.update { lista ->
            val nuevaLista = lista.filterNot { it == nombreMascota }
            
            // Actualizar contadores si se eliminó un elemento
            if (nuevaLista.size < lista.size) {
                _totalConsultasRealizadas.update { if (it > 0) it - 1 else 0 }
                _totalMascotasRegistradas.update { if (it > 0) it - 1 else 0 }
            }
            
            nuevaLista
        }
    }

    override fun editarMascota(textoOriginal: String, textoNuevo: String) {
        _listaMascotas.update { lista ->
            lista.map { if (it == textoOriginal) textoNuevo else it }
        }
    }
}
