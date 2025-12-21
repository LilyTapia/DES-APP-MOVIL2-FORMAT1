package cl.duoc.veterinaria.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Patterns
import cl.duoc.veterinaria.model.Usuario

enum class RecoveryStatus { IDLE, SUCCESS, ERROR }

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val usuariosSimulados = listOf(
        Usuario(1, "liliana", "liliana@gmail.com", "123456"),
        Usuario(2, "Colomba", "Colomba@gmail.com", "colomba123"),
        Usuario (id = 3, nombreUsuario = "Wilda", email = "Wilda@gmail.com", pass = "wilda1")
    )

    fun onLoginChange(user: String, pass: String) {
        _uiState.update {
            it.copy(
                user = user,
                pass = pass,
                userError = null,
                passError = null,
                loginError = null
            )
        }
    }

    fun onRecoveryEmailChange(email: String) {
        _uiState.update {
            it.copy(
                recoveryEmail = email,
                recoveryEmailError = null,
                recoveryStatus = RecoveryStatus.IDLE
            )
        }
    }

    fun login() {
        val user = _uiState.value.user
        val pass = _uiState.value.pass
        var userError: String? = null
        var passError: String? = null
        var formatIsValid = true

        if (user.contains("@")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                userError = "Formato de correo inválido"
                formatIsValid = false
            }
        } else if (user.isBlank()) {
            userError = "El campo de usuario no puede estar vacío"
            formatIsValid = false
        }

        if (pass.length < 6) {
            passError = "La contraseña debe tener al menos 6 caracteres"
            formatIsValid = false
        }

        if (!formatIsValid) {
            _uiState.update { it.copy(userError = userError, passError = passError) }
            return
        }

        val usuarioEncontrado = usuariosSimulados.find { it.nombreUsuario == user || it.email == user }

        if (usuarioEncontrado != null && usuarioEncontrado.pass == pass) {
            _uiState.update { it.copy(isLoggedIn = true) }
        } else {
            _uiState.update { it.copy(loginError = "Usuario o contraseña incorrectos") }
        }
    }

    fun requestPasswordRecovery() {
        val email = _uiState.value.recoveryEmail
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(recoveryEmailError = "Formato de correo inválido") }
            return
        }

        val emailExists = usuariosSimulados.any { it.email == email }
        if (emailExists) {
            _uiState.update { it.copy(recoveryStatus = RecoveryStatus.SUCCESS) }
        } else {
            _uiState.update { it.copy(recoveryStatus = RecoveryStatus.ERROR) }
        }
    }

    fun resetRecoveryStatus() {
        _uiState.update { it.copy(recoveryStatus = RecoveryStatus.IDLE, recoveryEmail = "", recoveryEmailError = null) }
    }
}

data class LoginUiState(
    val user: String = "",
    val pass: String = "",
    val isLoggedIn: Boolean = false,
    val userError: String? = null,
    val passError: String? = null,
    val loginError: String? = null,
    // Estados para la recuperación de contraseña
    val recoveryEmail: String = "",
    val recoveryEmailError: String? = null,
    val recoveryStatus: RecoveryStatus = RecoveryStatus.IDLE
)
