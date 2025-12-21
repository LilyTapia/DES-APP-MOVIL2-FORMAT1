package cl.duoc.veterinaria.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    val uiState by loginViewModel.uiState.collectAsState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (uiState.isLoggedIn) {
        onLoginSuccess()
    }

    if (showForgotPasswordDialog) {
        RecoveryDialog(
            uiState = uiState,
            onDismiss = { showForgotPasswordDialog = false; loginViewModel.resetRecoveryStatus() },
            onEmailChange = loginViewModel::onRecoveryEmailChange,
            onRecoverClick = loginViewModel::requestPasswordRecovery
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Logo Veterinaria",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Sistema Veterinaria", style = MaterialTheme.typography.headlineLarge)
        Text("Inicio de Sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.user,
            onValueChange = { loginViewModel.onLoginChange(it, uiState.pass) },
            label = { Text("Usuario o Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = uiState.userError != null || uiState.loginError != null,
            supportingText = { uiState.userError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { loginViewModel.onLoginChange(uiState.user, it) },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = uiState.passError != null || uiState.loginError != null,
            supportingText = { uiState.passError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier
                .clickable { showForgotPasswordDialog = true }
                .align(Alignment.End),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        uiState.loginError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { loginViewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.user.isNotBlank() && uiState.pass.isNotBlank()
        ) {
            Text("INICIAR SESIÓN")
        }
    }
}

@Composable
fun RecoveryDialog(
    uiState: LoginUiState,
    onDismiss: () -> Unit,
    onEmailChange: (String) -> Unit,
    onRecoverClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recuperar Contraseña") },
        text = {
            Column {
                when (uiState.recoveryStatus) {
                    RecoveryStatus.SUCCESS -> {
                        Text("Se ha enviado un enlace de recuperación a '${uiState.recoveryEmail}'. Cierra este diálogo.")
                    }
                    RecoveryStatus.ERROR -> {
                        Text("El correo '${uiState.recoveryEmail}' no está registrado. Por favor, inténtalo de nuevo.")
                    }
                    RecoveryStatus.IDLE -> {
                        Text("Ingresa tu correo electrónico para enviarte un enlace de recuperación.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.recoveryEmail,
                            onValueChange = onEmailChange,
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            isError = uiState.recoveryEmailError != null,
                            supportingText = { uiState.recoveryEmailError?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.recoveryStatus == RecoveryStatus.IDLE) {
                Button(onClick = onRecoverClick) {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(if (uiState.recoveryStatus == RecoveryStatus.IDLE) "Cancelar" else "Cerrar")
            }
        }
    )
}
