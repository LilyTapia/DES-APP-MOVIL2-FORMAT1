package cl.duoc.veterinaria.ui.registro

import cl.duoc.veterinaria.model.Consulta
import cl.duoc.veterinaria.model.DetallePedido
import cl.duoc.veterinaria.model.Pedido
import cl.duoc.veterinaria.model.TipoServicio
import java.time.LocalDate

/**
 * Estado de la UI para el flujo de registro.
 * Agrupa todos los datos necesarios para las pantallas de registro.
 */
data class RegistroUiState(
    // Datos del Dueño
    val duenoNombre: String = "",
    val duenoTelefono: String = "",
    val duenoEmail: String = "",

    // Datos de la Mascota
    val mascotaNombre: String = "",
    val mascotaEspecie: String = "",
    val mascotaEdad: String = "0",
    val mascotaPeso: String = "0.0",
    val mascotaUltimaVacuna: String = LocalDate.now().toString(),

    // Datos del Servicio
    val tipoServicio: TipoServicio? = null,
    val recomendacionVacuna: String? = null, // Campo para la recomendación del MascotaService

    // Datos del Pedido (Medicamentos)
    val carrito: List<DetallePedido> = emptyList(),

    // Resultado
    val consultaRegistrada: Consulta? = null,
    val pedidoRegistrado: Pedido? = null, // Almacenamos el pedido final confirmado
    val isProcesando: Boolean = false,
    
    // Mensaje de carga para mostrar durante el progreso
    val mensajeProgreso: String? = null
)
