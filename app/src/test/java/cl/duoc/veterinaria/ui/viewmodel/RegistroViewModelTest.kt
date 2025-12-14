package cl.duoc.veterinaria.ui.viewmodel

import android.content.Context
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.model.Medicamento
import cl.duoc.veterinaria.model.TipoServicio
import cl.duoc.veterinaria.ui.registro.RegistroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private lateinit var viewModel: RegistroViewModel
    private val testDispatcher = StandardTestDispatcher()

    // Mock simple del repositorio
    private val mockRepository = object : IVeterinariaRepository {
        override val totalMascotasRegistradas = MutableStateFlow(0)
        override val totalConsultasRealizadas = MutableStateFlow(0)
        override val nombreUltimoDueno = MutableStateFlow("")
        override val listaMascotas = MutableStateFlow<List<String>>(emptyList())
        // Nuevo campo agregado
        override val ultimaAtencionTipo = MutableStateFlow<String?>(null)
        
        // Firma actualizada con tipoServicio
        override fun registrarAtencion(nombreDueno: String, cantidadMascotas: Int, nombreMascota: String, especieMascota: String, tipoServicio: String?) {
            // No-op para test, pero actualizamos el estado simulado si es necesario
            ultimaAtencionTipo.value = tipoServicio
        }

        override fun init(context: Context) {
            // No-op
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistroViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateDatosDueno_actualiza_correctamente_el_estado() = runTest {
        val nombre = "Juan Perez"
        val telefono = "123456789"
        val email = "juan@test.com"

        viewModel.updateDatosDueno(nombre, telefono, email)

        val state = viewModel.uiState.value
        assertEquals(nombre, state.duenoNombre)
        assertEquals(telefono, state.duenoTelefono)
        assertEquals(email, state.duenoEmail)
    }

    @Test
    fun agregarMedicamentoAlCarrito_incrementa_cantidad_si_ya_existe() = runTest {
        val medicamento = Medicamento("Test Med", 100, 1000.0)
        
        viewModel.agregarMedicamentoAlCarrito(medicamento)
        viewModel.agregarMedicamentoAlCarrito(medicamento)

        val state = viewModel.uiState.value
        assertEquals(1, state.carrito.size)
        assertEquals(2, state.carrito.first().cantidad)
    }

    @Test
    fun procesarRegistro_genera_consulta_correctamente() = runTest {
        // Configurar datos mínimos
        viewModel.updateDatosDueno("Test", "123", "test@mail.com")
        viewModel.updateDatosMascota("Bobby", "Perro", "5", "10.0")
        viewModel.updateTipoServicio(TipoServicio.CONTROL)

        viewModel.procesarRegistro()
        testDispatcher.scheduler.advanceUntilIdle() // Ejecutar corrutinas pendientes

        val state = viewModel.uiState.value
        assertNotNull(state.consultaRegistrada)
        assertEquals("Consulta para Bobby", state.consultaRegistrada?.descripcion)
    }

    @Test
    fun resetEstado_limpia_datos_de_proceso_pero_mantiene_formulario() = runTest {
        // 1. Simular un proceso finalizado
        viewModel.updateDatosDueno("Juan", "123", "juan@test.com")
        viewModel.procesarRegistro()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull(viewModel.uiState.value.consultaRegistrada)

        // 2. Ejecutar resetEstado
        viewModel.resetEstado()

        val state = viewModel.uiState.value
        // Verificar que se limpió el resultado del proceso
        assertEquals(null, state.consultaRegistrada)
        assertEquals(false, state.isProcesando)
        // Verificar que se mantienen los datos del formulario
        assertEquals("Juan", state.duenoNombre)
    }
}
