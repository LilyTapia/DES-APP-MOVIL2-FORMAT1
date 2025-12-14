package cl.duoc.veterinaria.ui.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.util.ValidationUtils

/**
 * DuenoScreen es el Composable para la pantalla donde se ingresan los datos del dueño de la mascota.
 *
 * @param viewModel El ViewModel que contiene el estado del formulario de registro.
 * @param onNextClicked La acción a ejecutar cuando se hace clic en el botón "Siguiente".
 */
@Composable
fun DuenoScreen(viewModel: RegistroViewModel, onNextClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Validaciones:
    // Utilizando ValidationUtils para centralizar la lógica
    val isNombreValido = ValidationUtils.isValidNombre(uiState.duenoNombre)
    val isTelefonoValido = uiState.duenoTelefono.isNotBlank() && uiState.duenoTelefono.all { it.isDigit() }
    val isEmailValido = ValidationUtils.isValidEmail(uiState.duenoEmail)
    
    val isFormValid = isNombreValido && isTelefonoValido && isEmailValido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Muestra una imagen en la parte superior de la pantalla.
        Image(
            painter = painterResource(id = R.drawable.perrito),
            contentDescription = "Logo Veterinaria",
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        // Título de la pantalla.
        Text("Datos del Dueño", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Campo de texto para el nombre del cliente.
        RegistroTextField(
            value = uiState.duenoNombre,
            label = "Nombre del cliente",
            onValueChange = { viewModel.updateDatosDueno(nombre = it) },
            isError = !isNombreValido && uiState.duenoNombre.isNotBlank() // Mostrar error si no es válido y no está vacío
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de texto para el teléfono de contacto.
        // Se restringe el teclado a numérico (Phone) y se valida que sean solo números.
        RegistroTextField(
            value = uiState.duenoTelefono,
            label = "Teléfono de contacto (solo números)",
            onValueChange = { 
                // Opcional: Impedir ingreso de caracteres no numéricos directamente
                if (it.all { char -> char.isDigit() }) {
                    viewModel.updateDatosDueno(telefono = it) 
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = !isTelefonoValido && uiState.duenoTelefono.isNotBlank()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo de texto para el email.
        RegistroTextField(
            value = uiState.duenoEmail,
            label = "Email (para recordatorios)",
            onValueChange = { viewModel.updateDatosDueno(email = it) },
            isError = !isEmailValido && uiState.duenoEmail.isNotBlank(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón para navegar a la siguiente pantalla.
        Button(
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid // Se deshabilita si los campos no son válidos
        ) {
            Text("Siguiente")
        }
    }
}
