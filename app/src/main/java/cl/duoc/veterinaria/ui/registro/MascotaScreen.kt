package cl.duoc.veterinaria.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel

/**
 * MascotaScreen es el Composable para la pantalla donde se ingresan los datos de la mascota.
 *
 * @param viewModel El ViewModel que contiene el estado del formulario de registro.
 * @param onNextClicked La acción a ejecutar cuando se hace clic en el botón "Siguiente".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotaScreen(viewModel: RegistroViewModel, onNextClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Lista de especies predefinidas para el Dropdown
    val especies = listOf("Perro", "Gato", "Hamster", "Conejo", "Ave", "Otro")
    var expanded by remember { mutableStateOf(false) }

    // Validaciones simples para habilitar el botón
    val isFormValid = uiState.mascotaNombre.isNotBlank() &&
                      uiState.mascotaEspecie.isNotBlank() &&
                      (uiState.mascotaEdad.toIntOrNull() ?: -1) >= 0 &&
                      (uiState.mascotaPeso.toDoubleOrNull() ?: -1.0) > 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla.
        Text("Datos de la Mascota", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Campo de texto para el nombre de la mascota.
        RegistroTextField(
            value = uiState.mascotaNombre,
            label = "Nombre",
            onValueChange = { viewModel.updateDatosMascota(nombre = it) },
            isError = uiState.mascotaNombre.isBlank()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // --- Selector de Especie con ExposedDropdownMenuBox (Mejora solicitada) ---
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.mascotaEspecie,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    isError = uiState.mascotaEspecie.isBlank()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    especies.forEach { especie ->
                        DropdownMenuItem(
                            text = { Text(text = especie) },
                            onClick = {
                                viewModel.updateDatosMascota(especie = especie)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de texto para la edad.
        RegistroTextField(
            value = uiState.mascotaEdad,
            label = "Edad (años)",
            onValueChange = { viewModel.updateDatosMascota(edad = it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = (uiState.mascotaEdad.toIntOrNull() ?: -1) < 0
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de texto para el peso.
        RegistroTextField(
            value = uiState.mascotaPeso,
            label = "Peso (kg)",
            onValueChange = { viewModel.updateDatosMascota(peso = it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = (uiState.mascotaPeso.toDoubleOrNull() ?: -1.0) <= 0.0
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de texto para la última vacuna.
        RegistroTextField(
            value = uiState.mascotaUltimaVacuna,
            label = "Última vacuna (yyyy-MM-dd)",
            onValueChange = { viewModel.updateDatosMascota(ultimaVacuna = it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón para navegar a la siguiente pantalla.
        Button(
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid // Se habilita solo si el formulario es válido
        ) {
            Text("Siguiente")
        }
    }
}
