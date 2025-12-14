package cl.duoc.veterinaria.ui.registro

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.veterinaria.service.AgendaVeterinario
import cl.duoc.veterinaria.util.ReflectionUtils
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.util.oVacio

/**
 * ResumenScreen es el Composable para la pantalla que muestra el resumen de la consulta registrada.
 * Utiliza utilidades de reflexión y sobrecarga de operadores (si aplica) para mostrar información avanzada.
 *
 * @param viewModel El ViewModel que contiene el estado del formulario de registro y el resultado de la consulta.
 * @param onConfirmClicked La acción a ejecutar cuando se hace clic en el botón de confirmación.
 */
@Composable
fun ResumenScreen(viewModel: RegistroViewModel, onConfirmClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current // Contexto para lanzar Intents

    // LaunchedEffect se usa para ejecutar la lógica de procesamiento del registro una sola vez
    // cuando se compone esta pantalla.
    LaunchedEffect(Unit) {
        viewModel.procesarRegistro()
    }

    // Obtiene la consulta registrada desde el ViewModel.
    val consulta = uiState.consultaRegistrada
    val pedido = uiState.pedidoRegistrado

    // Determinamos si es una consulta completa o una venta directa
    val esVentaDirecta = uiState.duenoNombre.isBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilitamos scroll por si el contenido es largo
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (esVentaDirecta) "Resumen Venta Farmacia" else "Resumen Final", 
             style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Animación de visibilidad para el indicador de progreso
        AnimatedVisibility(
            visible = uiState.isProcesando,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                // Mensaje dinámico que cambia según el estado ("Cargando datos...", "Generando resumen...")
                Text(uiState.mensajeProgreso.oVacio("Procesando..."))
            }
        }

        // Muestra los detalles de la consulta si ya ha sido registrada.
        AnimatedVisibility(
            visible = !uiState.isProcesando,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                // Solo mostramos la tarjeta de consulta si NO es venta directa (es decir, si hubo registro)
                if (consulta != null && !esVentaDirecta) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalle Consulta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("ID: ${consulta.idConsulta}")
                            Text("Descripción: ${consulta.descripcion}")
                            Text("Fecha: ${consulta.fechaAtencion?.let { AgendaVeterinario.fmt(it) } ?: "No asignada"}")
                            Text("Veterinario: ${consulta.veterinarioAsignado?.nombre ?: "No asignado"}")
                            
                            // Mostrar recomendación de MascotaService si existe
                            if (uiState.recomendacionVacuna != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Plan de Vacunación: ${uiState.recomendacionVacuna}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Costo Consulta: $${consulta.costoConsulta}", fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Info Técnica:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = ReflectionUtils.describir(consulta),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mostrar detalle del pedido si existe
                if (pedido != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalle Pedido Farmacia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Mostrar nombre del cliente solo si es venta directa para contexto
                            if (esVentaDirecta) {
                                Text("Cliente: Venta de Mostrador (Anónimo)")
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            pedido.detalles.forEach { detalle ->
                                Text("- ${detalle.cantidad}x ${detalle.medicamento.nombre}: $${detalle.subtotal}")
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Total Pedido: $${pedido.total}", fontWeight = FontWeight.Bold)
                            if (pedido.total < pedido.totalSinPromocion()) {
                                Text("(Descuento aplicado)", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Total Global
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Si es venta directa, el costo de consulta es 0 visualmente (aunque el objeto exista internamente)
                        val costoConsulta = if (esVentaDirecta) 0.0 else (consulta?.costoConsulta ?: 0.0)
                        val costoPedido = pedido?.total ?: 0.0
                        
                        Text("TOTAL A PAGAR", style = MaterialTheme.typography.titleLarge)
                        Text("$${(costoConsulta + costoPedido).toInt()}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    }
                }

                // --- INTENT IMPLÍCITO: Botón Compartir ---
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val resumenTexto = """
                            Resumen Veterinaria:
                            Mascota: ${uiState.mascotaNombre}
                            Consulta: ${consulta?.descripcion ?: "N/A"}
                            Total: $${(if(esVentaDirecta) 0.0 else consulta?.costoConsulta ?: 0.0) + (pedido?.total ?: 0.0)}
                        """.trimIndent()

                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, resumenTexto)
                            type = "text/plain"
                        }
                        
                        val shareIntent = Intent.createChooser(sendIntent, "Compartir resumen con...")
                        
                        // FORZAMOS LA NOTIFICACIÓN REINICIANDO EL SERVICIO
                        // Pasamos EXPLICITAMENTE el tipo de servicio para asegurar que se notifique
                        // lo correcto inmediatamente.
                        val serviceIntent = Intent(context, cl.duoc.veterinaria.service.NotificacionService::class.java)
                        
                        // Obtenemos el tipo de servicio actual del ViewModel
                        uiState.tipoServicio?.let { tipoEnum ->
                            serviceIntent.putExtra("EXTRA_TIPO_ATENCION", tipoEnum.name)
                        }
                        
                        context.startService(serviceIntent)
                        
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isProcesando
                ) {
                    Text("Compartir Resumen")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Botón para confirmar y volver a la pantalla de inicio.
        Button(
            onClick = onConfirmClicked,
            modifier = Modifier.fillMaxWidth(),
            // El botón se habilita solo cuando el proceso ha terminado.
            enabled = !uiState.isProcesando
        ) {
            Text("Finalizar y Volver al Inicio")
        }
    }
}
