package cl.duoc.veterinaria.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import cl.duoc.veterinaria.model.Cliente
import cl.duoc.veterinaria.model.Consulta
import cl.duoc.veterinaria.model.DetallePedido
import cl.duoc.veterinaria.model.EstadoConsulta
import cl.duoc.veterinaria.model.Mascota
import cl.duoc.veterinaria.model.Medicamento
import cl.duoc.veterinaria.model.MedicamentoPromocional
import cl.duoc.veterinaria.model.Pedido
import cl.duoc.veterinaria.model.TipoServicio
import cl.duoc.veterinaria.service.AgendaVeterinario
import cl.duoc.veterinaria.service.ConsultaService
import cl.duoc.veterinaria.service.MascotaService
import cl.duoc.veterinaria.util.esDecimalValido
import cl.duoc.veterinaria.util.esNumeroValido
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

class RegistroViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    val catalogoMedicamentos = listOf(
        Medicamento("Antibiótico Generico", 500, 15000.0),
        Medicamento("Analgésico Básico", 200, 8000.0),
        MedicamentoPromocional("Antiinflamatorio Premium", 200, 25000.0, 0.20),
        MedicamentoPromocional("Vitaminas Caninas", 100, 12000.0, 0.10)
    )

    fun updateDatosDueno(nombre: String? = null, telefono: String? = null, email: String? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                duenoNombre = nombre ?: currentState.duenoNombre,
                duenoTelefono = telefono ?: currentState.duenoTelefono,
                duenoEmail = email ?: currentState.duenoEmail
            )
        }
    }

    fun updateDatosMascota(
        nombre: String? = null,
        especie: String? = null,
        edad: String? = null,
        peso: String? = null,
        ultimaVacuna: String? = null
    ) {
        _uiState.update { currentState ->
            var nuevoEdad = currentState.mascotaEdad
            if (edad != null && (edad.isEmpty() || edad.esNumeroValido())) {
                nuevoEdad = edad
            }
            var nuevoPeso = currentState.mascotaPeso
            if (peso != null && (peso.isEmpty() || peso.esDecimalValido())) {
                nuevoPeso = peso
            }
            currentState.copy(
                mascotaNombre = nombre ?: currentState.mascotaNombre,
                mascotaEspecie = especie ?: currentState.mascotaEspecie,
                mascotaEdad = nuevoEdad,
                mascotaPeso = nuevoPeso,
                mascotaUltimaVacuna = ultimaVacuna ?: currentState.mascotaUltimaVacuna
            )
        }
    }

    fun updateTipoServicio(servicio: TipoServicio) {
        _uiState.update { it.copy(tipoServicio = servicio) }
    }

    fun agregarMedicamentoAlCarrito(medicamento: Medicamento) {
        _uiState.update { currentState ->
            val carritoActual = currentState.carrito.toMutableList()
            val index = carritoActual.indexOfFirst { it.medicamento.nombre == medicamento.nombre }

            if (index != -1) {
                val detalleExistente = carritoActual[index]
                carritoActual[index] = detalleExistente.copy(cantidad = detalleExistente.cantidad + 1)
            } else {
                carritoActual.add(DetallePedido(medicamento, 1))
            }
            currentState.copy(carrito = carritoActual)
        }
    }

    fun quitarMedicamentoDelCarrito(medicamento: Medicamento) {
        _uiState.update { currentState ->
            val carritoActual = currentState.carrito.toMutableList()
            val index = carritoActual.indexOfFirst { it.medicamento.nombre == medicamento.nombre }

            if (index != -1) {
                val detalleExistente = carritoActual[index]
                if (detalleExistente.cantidad > 1) {
                    carritoActual[index] = detalleExistente.copy(cantidad = detalleExistente.cantidad - 1)
                } else {
                    carritoActual.removeAt(index)
                }
            }
            currentState.copy(carrito = carritoActual)
        }
    }

    fun procesarRegistro() {
        val currentState = _uiState.value
        if (currentState.consultaRegistrada != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcesando = true, mensajeProgreso = "Guardando...") }
            
            // ELIMINAMOS DELAYS para asegurar guardado inmediato
            // delay(1500) 

            val nombreCliente = if (currentState.duenoNombre.isNotBlank()) currentState.duenoNombre else "Cliente Anónimo"
            val emailCliente = if (currentState.duenoEmail.isNotBlank()) currentState.duenoEmail else "anonimo@mail.com"
            val telefonoCliente = if (currentState.duenoTelefono.isNotBlank()) currentState.duenoTelefono else "00000000"
            
            val clientePedido = Cliente(nombreCliente, emailCliente, telefonoCliente)

            val mascota = Mascota(
                nombre = currentState.mascotaNombre,
                especie = currentState.mascotaEspecie,
                edad = currentState.mascotaEdad.toIntOrNull() ?: 0,
                pesoKg = currentState.mascotaPeso.toDoubleOrNull() ?: 0.0,
                ultimaVacunacion = try { LocalDate.parse(currentState.mascotaUltimaVacuna) } catch (e: Exception) { LocalDate.now() }
            )

            val servicio = currentState.tipoServicio ?: TipoServicio.CONTROL
            val minutosEstimados = 30

            val fechaSugerida = AgendaVeterinario.siguienteSlotHabil(Clock.systemDefaultZone())
            val (veterinarioAsignado, slots) = AgendaVeterinario.reservarBloque(fechaSugerida, 1)

            val costoBase = ConsultaService.calcularCostoBase(servicio, minutosEstimados)
            val (costoConDescuento, _) = ConsultaService.aplicarDescuento(costoBase, 1)
            val costoFinal = ConsultaService.redondearClp(costoConDescuento)

            var recomendacionVacuna: String? = null
            var comentariosExtra: String? = null
            
            if (servicio == TipoServicio.VACUNA) {
                val descripcionFreq = MascotaService.descripcionFrecuencia(mascota)
                val proximaFecha = MascotaService.calcularProximaVacunacion(mascota)
                val fechaHoraVacuna = proximaFecha.atStartOfDay()
                recomendacionVacuna = "$descripcionFreq. Próxima dosis: ${AgendaVeterinario.fmt(fechaHoraVacuna)}"
                comentariosExtra = "Se aplicó protocolo según $descripcionFreq."
            }

            val nuevaConsulta = Consulta(
                idConsulta = "C-" + (100..999).random(),
                descripcion = "Consulta para ${mascota.nombre}",
                costoConsulta = costoFinal,
                estado = EstadoConsulta.PENDIENTE,
                fechaAtencion = slots.firstOrNull(),
                veterinarioAsignado = veterinarioAsignado,
                comentarios = comentariosExtra
            )

            var nuevoPedido: Pedido? = null
            if (currentState.carrito.isNotEmpty()) {
                nuevoPedido = Pedido(clientePedido, currentState.carrito)
            }

            // Guardado síncrono inmediato
            repository.registrarAtencion(nombreCliente, 1, mascota.nombre, mascota.especie, servicio.name)

            _uiState.update {
                it.copy(
                    consultaRegistrada = nuevaConsulta,
                    pedidoRegistrado = nuevoPedido,
                    recomendacionVacuna = recomendacionVacuna,
                    isProcesando = false
                )
            }
        }
    }

    fun resetEstado() {
        _uiState.update {
            it.copy(
                isProcesando = false,
                mensajeProgreso = null,
                consultaRegistrada = null,
                pedidoRegistrado = null,
                recomendacionVacuna = null
            )
        }
    }

    fun clearData() {
        _uiState.value = RegistroUiState()
    }
}
