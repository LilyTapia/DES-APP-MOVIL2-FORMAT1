package cl.duoc.veterinaria.util

import java.util.regex.Pattern

object ValidationUtils {
    private val EMAIL_REGEX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val NOMBRE_REGEX = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s'-]+$")

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matcher(email.trim()).matches()
    }

    fun isValidNombre(nombre: String): Boolean {
        return nombre.isNotBlank() && NOMBRE_REGEX.matcher(nombre).matches()
    }
}
