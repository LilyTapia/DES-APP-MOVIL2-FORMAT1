package cl.duoc.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ConsultaViewModel gestiona los datos relacionados con las consultas veterinarias y listados.
 * @param repository Inyección de dependencia del repositorio.
 */
class ConsultaViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    // Lista de mascotas registradas obtenida a través de la interfaz
    val listaPacientes: StateFlow<List<String>> = repository.listaMascotas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Función para eliminar mascota, que se conecta al repositorio
    fun eliminarConsulta(mascota: String) {
        repository.eliminarMascota(mascota)
    }

    // Función para editar mascota (FALTANTE)
    fun editarConsulta(original: String, nuevo: String) {
        repository.editarMascota(original, nuevo)
    }
}
