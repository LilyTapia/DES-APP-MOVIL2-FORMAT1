package cl.duoc.veterinaria.ui.viewmodel

import android.content.Context
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.model.Medicamento
import cl.duoc.veterinaria.model.TipoServicio
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private lateinit var viewModel: RegistroViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockRepository = object : IVeterinariaRepository {
        override val totalMascotasRegistradas = MutableStateFlow(0)
        override val totalConsultasRealizadas = MutableStateFlow(0)
        override val nombreUltimoDueno = MutableStateFlow("")
        override val listaMascotas = MutableStateFlow<List<String>>(emptyList())
        override val ultimaAtencionTipo = MutableStateFlow<String?>(null)

        override fun registrarAtencion(nombreDueno: String, cantidadMascotas: Int, nombreMascota: String, especieMascota: String, tipoServicio: String?) {
            ultimaAtencionTipo.value = tipoServicio
        }

        override fun init(context: Context) {}
        override fun eliminarMascota(nombreMascota: String) {
            
        }

        override fun editarMascota(textoOriginal: String, textoNuevo: String) {
            
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
    fun `updateDatosDueno should correctly update the state`() = runTest {
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
    fun `agregarMedicamentoAlCarrito should increment quantity if item already exists`() = runTest {
        val medicamento = Medicamento("Test Med", 100, 1000.0)

        viewModel.agregarMedicamentoAlCarrito(medicamento)
        viewModel.agregarMedicamentoAlCarrito(medicamento)

        val state = viewModel.uiState.value
        assertEquals(1, state.carrito.size)
        assertEquals(2, state.carrito.first().cantidad)
    }

    @Test
    fun `procesarRegistro should generate consulta correctly and update service state`() = runTest {
        viewModel.updateDatosDueno("Test", "123", "test@mail.com")
        viewModel.updateDatosMascota("Bobby", "Perro", "5", "10.0")
        viewModel.updateTipoServicio(TipoServicio.CONTROL)

        assertTrue(viewModel.serviceState.value is ServiceState.Idle)

        viewModel.procesarRegistro()
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertNotNull(uiState.consultaRegistrada)
        assertEquals("Consulta para Bobby", uiState.consultaRegistrada?.descripcion)

        assertTrue(viewModel.serviceState.value is ServiceState.Stopped)
    }

    @Test
    fun `clearData should reset ui and service states`() = runTest {
        viewModel.updateDatosDueno("Juan", "123", "juan@test.com")
        viewModel.procesarRegistro()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.consultaRegistrada)
        assertTrue(viewModel.serviceState.value is ServiceState.Stopped)

        viewModel.clearData()

        val uiState = viewModel.uiState.value
        assertEquals(null, uiState.consultaRegistrada)
        assertEquals("", uiState.duenoNombre)
        assertTrue(viewModel.serviceState.value is ServiceState.Idle)
    }
}