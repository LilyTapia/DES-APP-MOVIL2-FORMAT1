package cl.duoc.veterinaria.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.ui.viewmodel.ConsultaViewModel
import cl.duoc.veterinaria.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    onBack: () -> Unit,
    viewModel: ConsultaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val listaMascotas by viewModel.listaPacientes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val availableSpecies by viewModel.availableSpecies.collectAsState()
    val speciesFilter by viewModel.speciesFilter.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedMascota by remember { mutableStateOf("") }
    var editedText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Editar Consulta") },
            text = {
                Column {
                    Text("Modifique los datos:")
                    OutlinedTextField(value = editedText, onValueChange = { editedText = it }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editedText.isNotBlank()) {
                        viewModel.editarConsulta(selectedMascota, editedText)
                        Toast.makeText(context, "Editado correctamente", Toast.LENGTH_SHORT).show()
                        showDialog = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Listado de Mascotas") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(value = searchQuery, onValueChange = viewModel::onSearchQueryChange, label = { Text("Buscar por nombre...") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = it }) {
                OutlinedTextField(
                    value = speciesFilter ?: "Todas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por especie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    availableSpecies.forEach { species ->
                        DropdownMenuItem(
                            text = { Text(species) },
                            onClick = {
                                viewModel.onSpeciesFilterChange(species)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Text("Ordenar:", style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = { viewModel.onSortOrderChange(SortOrder.ASC) }) { Text("A-Z") }
                TextButton(onClick = { viewModel.onSortOrderChange(SortOrder.DESC) }) { Text("Z-A") }
                TextButton(onClick = { viewModel.onSortOrderChange(SortOrder.NONE) }) { Text("Reset") }
            }

            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (listaMascotas.isEmpty()) {
                    item {
                        Text("No se encontraron resultados para los filtros aplicados.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(listaMascotas) { mascota ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = mascota, style = MaterialTheme.typography.bodyLarge)
                                }
                                Row {
                                    IconButton(onClick = { selectedMascota = mascota; editedText = mascota; showDialog = true }) {
                                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.eliminarConsulta(mascota); Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show() }) {
                                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
