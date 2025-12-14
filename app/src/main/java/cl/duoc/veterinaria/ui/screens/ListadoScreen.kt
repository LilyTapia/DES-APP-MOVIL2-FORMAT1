package cl.duoc.veterinaria.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

/**
 * Pantalla que muestra el listado de mascotas registradas.
 * Permite editar y eliminar registros directamente desde la lista.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    onBack: () -> Unit,
    viewModel: ConsultaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val listaMascotas by viewModel.listaPacientes.collectAsState()
    val context = LocalContext.current

    // Estados para controlar el diálogo de edición
    var showDialog by remember { mutableStateOf(false) }
    var selectedMascota by remember { mutableStateOf("") }
    var editedText by remember { mutableStateOf("") }

    // Diálogo emergente para editar la información de la mascota
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Editar Consulta") },
            text = {
                Column {
                    Text("Modifique los datos:")
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedText.isNotBlank()) {
                            viewModel.editarConsulta(selectedMascota, editedText)
                            Toast.makeText(context, "Editado correctamente", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Listado de Mascotas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (listaMascotas.isEmpty()) {
                item {
                    Text(
                        text = "No hay mascotas registradas aún.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(listaMascotas) { mascota ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Muestra el detalle de la mascota
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mascota,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            
                            // Botones de Acción (Editar / Eliminar)
                            Row {
                                // Botón Editar: Abre el diálogo con el texto actual
                                IconButton(onClick = { 
                                    selectedMascota = mascota
                                    editedText = mascota
                                    showDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                // Botón Eliminar: Borra el registro inmediatamente
                                IconButton(onClick = { 
                                    viewModel.eliminarConsulta(mascota)
                                    Toast.makeText(context, "Eliminado correctamente", Toast.LENGTH_SHORT).show() 
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
