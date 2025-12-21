package cl.duoc.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class SortOrder {
    NONE,
    ASC,
    DESC
}

class ConsultaViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    private val _speciesFilter = MutableStateFlow<String?>(null)
    val speciesFilter = _speciesFilter.asStateFlow()

    // Expone la lista de especies únicas para la UI
    val availableSpecies: StateFlow<List<String>> = repository.listaMascotas
        .map { mascotas ->
            // Extrae la especie de cada string, elimina duplicados y añade "Todas" al principio
            listOf("Todas") + mascotas.mapNotNull { it.substringAfter('(').substringBefore(')') }.distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todas"))

    // Combina todos los filtros: búsqueda, especie y ordenamiento
    val listaPacientes: StateFlow<List<String>> = combine(
        repository.listaMascotas, _searchQuery, _speciesFilter, _sortOrder
    ) { list, query, species, order ->
        val filteredBySearch = if (query.isBlank()) {
            list
        } else {
            list.filter { it.contains(query, ignoreCase = true) }
        }

        val filteredBySpecies = if (species == null) {
            filteredBySearch
        } else {
            filteredBySearch.filter { it.contains("($species)") }
        }

        when (order) {
            SortOrder.ASC -> filteredBySpecies.sorted()
            SortOrder.DESC -> filteredBySpecies.sortedDescending()
            SortOrder.NONE -> filteredBySpecies
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
    }

    fun onSpeciesFilterChange(species: String) {
        _speciesFilter.value = if (species == "Todas") null else species
    }

    fun eliminarConsulta(mascota: String) {
        repository.eliminarMascota(mascota)
    }

    fun editarConsulta(original: String, nuevo: String) {
        repository.editarMascota(original, nuevo)
    }
}
