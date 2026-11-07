package com.padroniza.app.ui.consulta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.data.local.entities.*
import com.padroniza.app.data.repository.ConsultaRepository
import com.padroniza.app.ui.theme.*
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaConsultaScreen(
    frigorificos: List<FrigorificoEntity>,
    categorias: List<CategoriaEntity>,
    clientes: List<ClienteEntity>,
    fazendasDoCliente: List<FazendaEntity>,
    gruposDoCliente: List<GrupoWhatsappEntity>,
    onCarregarFazendasEGrupos: (String) -> Unit,
    onSalvarConsulta: (ConsultaEntity) -> Unit,
    onVoltar: () -> Unit
) {
    var etapaAtual by remember { mutableIntStateOf(1) }

    // Estado da Consulta
    var selectedFrigorifico by remember { mutableStateOf<FrigorificoEntity?>(null) }
    var selectedCliente by remember { mutableStateOf<ClienteEntity?>(null) }
    var selectedFazenda by remember { mutableStateOf<FazendaEntity?>(null) }
    var selectedCategoria by remember { mutableStateOf<CategoriaEntity?>(null) }
    var selectedSexo by remember { mutableStateOf("MACHO") }
    var selectedGrupos by remember { mutableStateOf<Set<String>>(emptySet()) }
    var dataAbate by remember { mutableStateOf("2026-11-05") }

    // Pesos
    var qtdAnimaisTexto by remember { mutableStateOf("50") }
    var pesoVivoTexto by remember { mutableStateOf("520.0") }
    var pesoTotalLoteTexto by remember { mutableStateOf("13500.0") }
    var pesoCarcacaTexto by remember { mutableStateOf("270.0") }

    // Dentição
    var d0 by remember { mutableIntStateOf(20) }
    var d2 by remember { mutableIntStateOf(30) }
    var d4 by remember { mutableIntStateOf(0) }
    var d6 by remember { mutableIntStateOf(0) }
    var d8 by remember { mutableIntStateOf(0) }

    // Gordura
    var g1 by remember { mutableIntStateOf(0) }
    var g2 by remember { mutableIntStateOf(10) }
    var g3 by remember { mutableIntStateOf(35) }
    var g4 by remember { mutableIntStateOf(5) }
    var g5 by remember { mutableIntStateOf(0) }

    // Adicionais
    var penalizacoes by remember { mutableStateOf("") }
    var dif by remember { mutableStateOf("") }
    var escoreRuminal by remember { mutableStateOf("Grau 3") }

    val qtdInt = qtdAnimaisTexto.toIntOrNull() ?: 0
    val pVivo = pesoVivoTexto.toDoubleOrNull() ?: 0.0
    val pTotal = pesoTotalLoteTexto.toDoubleOrNull() ?: 0.0
    val pCarcaca = pesoCarcacaTexto.toDoubleOrNull() ?: 0.0

    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember(context) { ConsultaRepository(context) }
    val metricas = remember(qtdInt, pTotal, pVivo, d0, d2, d4, d6, d8, g1, g2, g3, g4, g5) {
        repo.calcularMetricas(qtdInt, pTotal, pVivo, d0, d2, d4, d6, d8, g1, g2, g3, g4, g5)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Abate (Etapa $etapaAtual/5)", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        bottomBar = {
            Surface(color = SurfaceLight, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (etapaAtual > 1) {
                        OutlinedButton(
                            onClick = { etapaAtual-- },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Anterior")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (etapaAtual < 5) {
                        Button(
                            onClick = { etapaAtual++ },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Avançar")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                val consultaFinal = ConsultaEntity(
                                    localId = "",
                                    clienteId = selectedCliente?.id ?: "",
                                    clienteNome = selectedCliente?.nomeProdutor ?: "",
                                    fazendaId = selectedFazenda?.id ?: "",
                                    fazendaNome = selectedFazenda?.nome ?: "",
                                    frigorificoId = selectedFrigorifico?.id ?: "",
                                    frigorificoNome = selectedFrigorifico?.nome ?: "",
                                    categoriaAnimalId = selectedCategoria?.id ?: "",
                                    categoriaNome = selectedCategoria?.nome ?: "",
                                    dataAbate = dataAbate,
                                    gruposWhatsappIds = selectedGrupos.joinToString(","),
                                    qtdAnimais = qtdInt,
                                    sexoLote = selectedSexo,
                                    pesoVivoKg = pVivo,
                                    pesoTotalLoteKg = pTotal,
                                    pesoCarcacaKg = pCarcaca,
                                    mediaArroba = metricas.mediaArroba,
                                    rcPercentual = metricas.rcPercentual,
                                    dentes0 = d0, dentes2 = d2, dentes4 = d4, dentes6 = d6, dentes8 = d8,
                                    dentes0Pct = metricas.dentes0Pct, dentes2Pct = metricas.dentes2Pct,
                                    dentes4Pct = metricas.dentes4Pct, dentes6Pct = metricas.dentes6Pct,
                                    dentes8Pct = metricas.dentes8Pct,
                                    gordura1 = g1, gordura2 = g2, gordura3 = g3, gordura4 = g4, gordura5 = g5,
                                    gordura1Pct = metricas.gordura1Pct, gordura2Pct = metricas.gordura2Pct,
                                    gordura3Pct = metricas.gordura3Pct, gordura4Pct = metricas.gordura4Pct,
                                    gordura5Pct = metricas.gordura5Pct,
                                    penalizacoes = penalizacoes.ifBlank { null },
                                    dif = dif.ifBlank { null },
                                    escoreRuminal = escoreRuminal.ifBlank { null },
                                    statusEnvio = StatusEnvio.PENDENTE_ENVIO
                                )
                                onSalvarConsulta(consultaFinal)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusEnviado)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Finalizar e Enviar", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (etapaAtual) {
                1 -> {
                    Text("1. Identificação do Lote", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    // Seletor de Frigorífico
                    Text("Frigorífico", fontSize = 12.sp, color = TextSecondary)
                    frigorificos.forEach { frigo ->
                        CardItemSelect(
                            label = "${frigo.nome} (${frigo.cidadeNome}/${frigo.estadoUf})",
                            isSelected = selectedFrigorifico?.id == frigo.id,
                            onClick = { selectedFrigorifico = frigo }
                        )
                    }

                    // Seletor de Produtor
                    Text("Produtor / Cliente", fontSize = 12.sp, color = TextSecondary)
                    clientes.forEach { cli ->
                        CardItemSelect(
                            label = cli.nomeProdutor,
                            isSelected = selectedCliente?.id == cli.id,
                            onClick = {
                                selectedCliente = cli
                                selectedFazenda = null
                                onCarregarFazendasEGrupos(cli.id)
                            }
                        )
                    }

                    // Seletor de Fazenda
                    if (fazendasDoCliente.isNotEmpty()) {
                        Text("Fazenda", fontSize = 12.sp, color = TextSecondary)
                        fazendasDoCliente.forEach { faz ->
                            CardItemSelect(
                                label = "${faz.nome} (${faz.cidadeNome})",
                                isSelected = selectedFazenda?.id == faz.id,
                                onClick = { selectedFazenda = faz }
                            )
                        }
                    }

                    // Seletor de Sexo
                    Text("Sexo do Lote", fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedSexo == "MACHO",
                            onClick = { selectedSexo = "MACHO" },
                            label = { Text("Machos") }
                        )
                        FilterChip(
                            selected = selectedSexo == "FEMEA",
                            onClick = { selectedSexo = "FEMEA" },
                            label = { Text("Fêmeas") }
                        )
                    }
                }

                2 -> {
                    Text("2. Pesagem e Rendimento", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = qtdAnimaisTexto,
                        onValueChange = { qtdAnimaisTexto = it },
                        label = { Text("Quantidade de Animais (cab)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pesoVivoTexto,
                        onValueChange = { pesoVivoTexto = it },
                        label = { Text("Peso Vivo Médio (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pesoTotalLoteTexto,
                        onValueChange = { pesoTotalLoteTexto = it },
                        label = { Text("Peso Total do Lote (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pesoCarcacaTexto,
                        onValueChange = { pesoCarcacaTexto = it },
                        label = { Text("Peso Carcaça Médio (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PadronizaRed.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Prévia em Tempo Real:", fontWeight = FontWeight.Bold, color = PadronizaRed)
                            Text("Média: ${metricas.mediaArroba} @", fontWeight = FontWeight.SemiBold)
                            Text("Rendimento de Carcaça: ${metricas.rcPercentual} %", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                3 -> {
                    Text("3. Maturidade (Dentição)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    ContadorDente("0 dentes", d0, metricas.dentes0Pct) { d0 = it }
                    ContadorDente("2 dentes", d2, metricas.dentes2Pct) { d2 = it }
                    ContadorDente("4 dentes", d4, metricas.dentes4Pct) { d4 = it }
                    ContadorDente("6 dentes", d6, metricas.dentes6Pct) { d6 = it }
                    ContadorDente("8 dentes", d8, metricas.dentes8Pct) { d8 = it }
                }

                4 -> {
                    Text("4. Acabamento de Gordura", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    ContadorDente("1 - Ausente", g1, metricas.gordura1Pct) { g1 = it }
                    ContadorDente("2 - Escasso", g2, metricas.gordura2Pct) { g2 = it }
                    ContadorDente("3 - Mediano", g3, metricas.gordura3Pct) { g3 = it }
                    ContadorDente("4 - Uniforme", g4, metricas.gordura4Pct) { g4 = it }
                    ContadorDente("5 - Excessivo", g5, metricas.gordura5Pct) { g5 = it }
                }

                5 -> {
                    Text("5. Observações e Confirmação", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = penalizacoes,
                        onValueChange = { penalizacoes = it },
                        label = { Text("Penalizações (opcional)") },
                        placeholder = { Text("Ex: Hematomas ou contaminações") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dif,
                        onValueChange = { dif = it },
                        label = { Text("D.I.F. (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Resumo do Envio Offline:", fontWeight = FontWeight.Bold)
                            Text("• Produtor: ${selectedCliente?.nomeProdutor ?: "-"}")
                            Text("• Fazenda: ${selectedFazenda?.nome ?: "-"}")
                            Text("• Frigorífico: ${selectedFrigorifico?.nome ?: "-"}")
                            Text("• Total: $qtdInt cab (${metricas.mediaArroba}@ / RC ${metricas.rcPercentual}%)")
                            Text("Ao finalizar, este lote será salvo no SQLite local e enviado pelo WorkManager assim que houver conexão.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardItemSelect(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PadronizaRed.copy(alpha = 0.12f) else SurfaceLight)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) PadronizaRed else BorderLight,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PadronizaRed else TextPrimary
        )
    }
}

@Composable
fun ContadorDente(label: String, valor: Int, percentual: Double, onMudar: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceLight)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("$percentual % do lote", fontSize = 11.sp, color = TextSecondary)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = { if (valor > 0) onMudar(valor - 1) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = BackgroundLight)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Diminuir")
            }

            Text("$valor", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextPrimary)

            IconButton(
                onClick = { onMudar(valor + 1) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PadronizaRed.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aumentar", tint = PadronizaRed)
            }
        }
    }
}
