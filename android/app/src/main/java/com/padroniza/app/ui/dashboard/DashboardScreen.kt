package com.padroniza.app.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    onReenviarConsulta: (String) -> Unit = {},
    onExcluirConsulta: (String) -> Unit = {},
    onEditarConsulta: (ConsultaEntity) -> Unit = {},
    onReenviarTodasComFalha: () -> Unit = {},
    onSelecionarConsulta: (String) -> Unit = {}
) {
    var consultaSelecionada by remember { mutableStateOf<ConsultaEntity?>(null) }
    var itensVisiveis by remember { mutableIntStateOf(5) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra Fixa Superior com Título e Botão "+ Novo Abate" Reposicionado
            Surface(
                color = SurfaceLight,
                border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Histórico de Abates",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${consultas.size} lote(s) registrado(s)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onNovaConsulta,
                        colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Novo Abate", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (consultas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Nenhum abate registrado ainda", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Toque no botão 'Novo Abate' acima para iniciar", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            } else {
                val consultasExibidas = consultas.take(itensVisiveis)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Banner de Alerta para Falhas
                    val falhas = consultas.count { it.statusEnvio == StatusEnvio.ERRO }
                    if (falhas > 0) {
                        item(key = "banner_falhas") {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = StatusErro.copy(alpha = 0.12f)),
                                border = BorderStroke(1.dp, StatusErro.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Atenção",
                                            tint = StatusErro
                                        )
                                        Column {
                                            Text(
                                                text = "$falhas lote(s) com falha no envio",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = StatusErro
                                            )
                                            Text(
                                                text = "Toque no card para corrigir ou reenvie todos.",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = onReenviarTodasComFalha,
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusErro),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Text("Reenviar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Banner de Fila Offline Pendente
                    val pendentes = consultas.count { it.statusEnvio == StatusEnvio.PENDENTE_ENVIO }
                    if (pendentes > 0) {
                        item(key = "banner_pendentes") {
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

                    // Lista de Cards de Consulta
                    items(consultasExibidas, key = { it.localId }) { consulta ->
                        ConsultaCard(
                            consulta = consulta,
                            onClick = {
                                consultaSelecionada = consulta
                                onSelecionarConsulta(consulta.localId)
                            },
                            onReenviar = { onReenviarConsulta(consulta.localId) },
                            onEditar = { onEditarConsulta(consulta) }
                        )
                    }

                    // Rodapé com Botão de Paginação "Carregar mais"
                    if (consultas.size > itensVisiveis) {
                        item(key = "botao_carregar_mais") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { itensVisiveis += 5 },
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, PadronizaRed),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PadronizaRed),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                        Text(
                                            text = "Carregar mais (${consultas.size - itensVisiveis} restantes)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else if (consultas.size > 5) {
                        item(key = "fim_listagem") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Fim da listagem (todas as ${consultas.size} consultas carregadas)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    item(key = "espaco_final") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Modal BottomSheet de Detalhes e Ações da Consulta
    if (consultaSelecionada != null) {
        val consulta = consultaSelecionada!!
        val (mensagemErroAmigavel, precisaEditar) = formatarMensagemErro(consulta.ultimoErro)

        ModalBottomSheet(
            onDismissRequest = { consultaSelecionada = null },
            sheetState = sheetState,
            containerColor = SurfaceLight,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header com Produtor e Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = consulta.clienteNome,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "${consulta.fazendaNome} • ${consulta.frigorificoNome}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    StatusBadge(consulta.statusEnvio)
                }

                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

                // Bloco Específico de Falha
                if (consulta.statusEnvio == StatusEnvio.ERRO) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusErro.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, StatusErro.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusErro,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (precisaEditar) "INFORMAÇÕES PENDENTES" else "MOTIVO DA FALHA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StatusErro
                                )
                            }
                            Text(
                                text = mensagemErroAmigavel,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                            if (consulta.tentativasEnvio > 0) {
                                Text(
                                    text = "Tentativas realizadas: ${consulta.tentativasEnvio}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else if (consulta.statusEnvio == StatusEnvio.PENDENTE_ENVIO) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusPendente.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Aguardando envio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Este lote está salvo no aparelho e será enviado automaticamente quando houver conexão.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Resumo dos Dados do Lote
                Surface(
                    color = BackgroundLight,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Quantidade", fontSize = 12.sp, color = TextSecondary)
                            Text("${consulta.qtdAnimais} cab (${consulta.sexoLote})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Média Carcaça", fontSize = 12.sp, color = TextSecondary)
                            Text("${consulta.mediaArroba}@", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rendimento de Carcaça", fontSize = 12.sp, color = TextSecondary)
                            Text("${consulta.rcPercentual}%", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Data do Abate", fontSize = 12.sp, color = TextSecondary)
                            Text(consulta.dataAbate, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Botões de Ação Estratégicos
                if (consulta.statusEnvio == StatusEnvio.ERRO) {
                    if (precisaEditar) {
                        // ESTRATÉGIA: Quando faltam campos (erro de validação), o botão primário é EDITAR
                        Button(
                            onClick = {
                                val c = consulta
                                consultaSelecionada = null
                                onEditarConsulta(c)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Text("Editar e Corrigir Lote", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val id = consulta.localId
                                consultaSelecionada = null
                                onExcluirConsulta(id)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErro),
                            border = BorderStroke(1.dp, StatusErro.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Text("Excluir Lote do Aparelho", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Falha de Conexão: Reenviar é a ação primária
                        Button(
                            onClick = {
                                val id = consulta.localId
                                consultaSelecionada = null
                                onReenviarConsulta(id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Text("Reenviar Agora", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val c = consulta
                                consultaSelecionada = null
                                onEditarConsulta(c)
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Text("Editar Lote", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val id = consulta.localId
                                consultaSelecionada = null
                                onExcluirConsulta(id)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErro),
                            border = BorderStroke(1.dp, StatusErro.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Text("Excluir Lote do Aparelho", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (consulta.statusEnvio == StatusEnvio.PENDENTE_ENVIO) {
                    Button(
                        onClick = {
                            val id = consulta.localId
                            consultaSelecionada = null
                            onReenviarConsulta(id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Text("Forçar Envio Agora", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val id = consulta.localId
                            consultaSelecionada = null
                            onExcluirConsulta(id)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErro),
                        border = BorderStroke(1.dp, StatusErro.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Text("Excluir Lote do Aparelho", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { consultaSelecionada = null },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Fechar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultaCard(
    consulta: ConsultaEntity,
    onClick: () -> Unit,
    onReenviar: () -> Unit = {},
    onEditar: () -> Unit = {}
) {
    val isErro = consulta.statusEnvio == StatusEnvio.ERRO
    val (_, precisaEditar) = if (isErro) formatarMensagemErro(consulta.ultimoErro) else ("" to false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isErro) BorderStroke(1.dp, StatusErro.copy(alpha = 0.45f)) else null,
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

            // Ação Rápida Direta no Card quando for Falha
            if (isErro) {
                HorizontalDivider(color = StatusErro.copy(alpha = 0.2f), thickness = 0.5.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (precisaEditar) "Faltam dados • Toque para corrigir" else "Toque para ver detalhes",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusErro
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (precisaEditar) {
                            Button(
                                onClick = onEditar,
                                colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Text("Corrigir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = onReenviar,
                                colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("Reenviar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
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

/**
 * Traduz o erro técnico ou JSON de validação (Zod / 400) para uma mensagem em português claro
 * e retorna se o lote precisa ser editado (true) ou se é apenas uma falha de rede/timeout (false).
 */
fun formatarMensagemErro(erroBruto: String?): Pair<String, Boolean> {
    if (erroBruto.isNullOrBlank()) {
        return "Falha ao comunicar com o servidor ou dados rejeitados." to false
    }

    val ehErroValidacao = erroBruto.contains("fieldErrors") || erroBruto.contains("formErrors") || erroBruto.contains("400")

    if (ehErroValidacao) {
        val campos = mutableListOf<String>()
        if (erroBruto.contains("grupoWhatsappId")) campos.add("Grupo de WhatsApp não selecionado")
        if (erroBruto.contains("categoriaAnimalId")) campos.add("Categoria do animal não selecionada")
        if (erroBruto.contains("clienteId")) campos.add("Produtor / Cliente não selecionado")
        if (erroBruto.contains("fazendaId")) campos.add("Fazenda de origem não selecionada")
        if (erroBruto.contains("frigorificoId")) campos.add("Frigorífico não selecionado")
        if (erroBruto.contains("dataAbate")) campos.add("Data de abate inválida ou ausente")
        if (erroBruto.contains("pesoCarcacaKg")) campos.add("Peso de carcaça inconsistente com peso vivo")

        val explicacao = if (campos.isNotEmpty()) {
            "Faltam dados obrigatórios para este lote:\n• " + campos.joinToString("\n• ") + "\n\nToque em 'Editar e Corrigir Lote' para preencher as informações faltantes antes de enviar."
        } else {
            "O lote possui dados rejeitados pela validação da central. Toque em 'Editar e Corrigir Lote' para revisar."
        }
        return explicacao to true
    }

    if (erroBruto.contains("conexão", ignoreCase = true) ||
        erroBruto.contains("timeout", ignoreCase = true) ||
        erroBruto.contains("failed to connect", ignoreCase = true)
    ) {
        return "Sem conexão com a internet ou servidor temporariamente inacessível. Você pode tentar reenviar agora." to false
    }

    return erroBruto to false
}
