package com.padroniza.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.data.local.entities.StatusEnvio
import com.padroniza.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    consultorNome: String,
    consultas: List<ConsultaEntity>,
    onNovaConsulta: () -> Unit,
    onSincronizarManual: () -> Unit,
    onLogout: () -> Unit,
    onSelecionarConsulta: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("padroniza", fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(".ba", fontWeight = FontWeight.Black, fontSize = 20.sp, color = PadronizaRed)
                        }
                        Text("Olá, $consultorNome", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = onSincronizarManual) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sincronizar", tint = PadronizaRed)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovaConsulta,
                icon = { Icon(Icons.Default.Add, contentDescription = "Novo") },
                text = { Text("Novo Abate", fontWeight = FontWeight.Bold) },
                containerColor = PadronizaRed,
                contentColor = Color.White
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (consultas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nenhum abate registrado ainda", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Toque em '+ Novo Abate' para iniciar", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val pendentes = consultas.count { it.statusEnvio == StatusEnvio.PENDENTE_ENVIO }
                    if (pendentes > 0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusPendente.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("⏳", fontSize = 18.sp)
                                Column {
                                    Text(
                                        "$pendentes lote(s) na fila offline",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Serão enviados automaticamente assim que houver sinal de rede.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                items(consultas, key = { it.localId }) { consulta ->
                    ConsultaCard(consulta = consulta, onClick = { onSelecionarConsulta(consulta.localId) })
                }
            }
        }
    }
}

@Composable
fun ConsultaCard(consulta: ConsultaEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = consulta.clienteNome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                StatusBadge(consulta.statusEnvio)
            }

            Text(
                text = "${consulta.fazendaNome} • ${consulta.frigorificoNome}",
                fontSize = 13.sp,
                color = TextSecondary
            )

            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Lote", fontSize = 11.sp, color = TextSecondary)
                    Text("${consulta.qtdAnimais} cab (${consulta.sexoLote})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Média Carcaça", fontSize = 11.sp, color = TextSecondary)
                    Text("${consulta.mediaArroba}@", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Rendimento", fontSize = 11.sp, color = TextSecondary)
                    Text("${consulta.rcPercentual}%", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            val formatador = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text(
                text = "Registrado em ${formatador.format(Date(consulta.criadoEm))}",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StatusBadge(status: StatusEnvio) {
    val (label, bg, fg) = when (status) {
        StatusEnvio.ENVIADO -> Triple("Enviado", StatusEnviado.copy(alpha = 0.2f), StatusEnviado)
        StatusEnvio.PENDENTE_ENVIO -> Triple("Pendente", StatusPendente.copy(alpha = 0.2f), Color(0xFFB45309))
        StatusEnvio.RASCUNHO -> Triple("Rascunho", StatusRascunho.copy(alpha = 0.2f), StatusRascunho)
        StatusEnvio.ERRO -> Triple("Falha", StatusErro.copy(alpha = 0.2f), StatusErro)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
