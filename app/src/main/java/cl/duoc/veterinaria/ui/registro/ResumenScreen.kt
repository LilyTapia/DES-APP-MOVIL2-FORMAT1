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
import cl.duoc.veterinaria.service.NotificacionService
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.ui.viewmodel.ServiceState
import cl.duoc.veterinaria.util.ReflectionUtils

@Composable
fun ResumenScreen(viewModel: RegistroViewModel, onConfirmClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val serviceState by viewModel.serviceState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.procesarRegistro()
    }

    // --- Notificación Automática ---
    LaunchedEffect(uiState.notificacionAutomaticaMostrada) {
        if (uiState.notificacionAutomaticaMostrada) {
            val serviceIntent = Intent(context, NotificacionService::class.java).apply {
                putExtra("EXTRA_TITULO", "¡Consulta Registrada!")
                putExtra("EXTRA_TEXTO", "La consulta para '${uiState.mascotaNombre}' se ha guardado exitosamente.")
            }
            context.startService(serviceIntent)
            viewModel.onNotificacionMostrada() // Resetea la bandera
        }
    }

    val consulta = uiState.consultaRegistrada
    val pedido = uiState.pedidoRegistrado
    val esVentaDirecta = uiState.duenoNombre.isBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (esVentaDirecta) "Resumen Venta Farmacia" else "Resumen Final", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = serviceState is ServiceState.Running, enter = fadeIn(), exit = fadeOut()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                if (serviceState is ServiceState.Running) {
                    Text((serviceState as ServiceState.Running).message)
                }
            }
        }

        AnimatedVisibility(visible = serviceState is ServiceState.Stopped, enter = fadeIn(), exit = fadeOut()) {
            Column {
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

                            uiState.recomendacionVacuna?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Plan de Vacunación: $it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Costo Consulta: $${consulta.costoConsulta}", fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Info Técnica:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            Text(ReflectionUtils.describir(consulta), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (pedido != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalle Pedido Farmacia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        val costoConsulta = if (esVentaDirecta) 0.0 else (consulta?.costoConsulta ?: 0.0)
                        val costoPedido = pedido?.total ?: 0.0
                        Text("TOTAL A PAGAR", style = MaterialTheme.typography.titleLarge)
                        Text("$${(costoConsulta + costoPedido).toInt()}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // --- Notificación Manual ---
                        val serviceIntent = Intent(context, NotificacionService::class.java).apply {
                            putExtra("EXTRA_TITULO", "Resumen Compartido")
                            putExtra("EXTRA_TEXTO", "Se ha compartido el resumen de la consulta.")
                        }
                        context.startService(serviceIntent)

                        // --- Lógica para compartir ---
                        val resumenTexto = """
                            Resumen Veterinaria:
                            Mascota: ${uiState.mascotaNombre}
                            Consulta: ${consulta?.descripcion ?: "N/A"}
                            Total: $${(if (esVentaDirecta) 0.0 else consulta?.costoConsulta ?: 0.0) + (pedido?.total ?: 0.0)}
                        """.trimIndent()

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, resumenTexto)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Compartir resumen con...")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = serviceState is ServiceState.Stopped
                ) {
                    Text("Compartir Resumen")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onConfirmClicked, modifier = Modifier.fillMaxWidth(), enabled = serviceState is ServiceState.Stopped) {
            Text("Finalizar y Volver al Inicio")
        }
    }
}
