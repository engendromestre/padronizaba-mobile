package com.padroniza.app.ui.consulta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.data.local.entities.*
import com.padroniza.app.data.repository.ConsultaRepository
import com.padroniza.app.ui.consulta.components.FormProgress
import com.padroniza.app.ui.consulta.screens.*
import com.padroniza.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaConsultaScreen(
    frigorificos: List<FrigorificoEntity>,
    categorias: List<CategoriaEntity>,
    clientes: List<ClienteEntity>,
    fazendasDoCliente: List<FazendaEntity>,
    todosOsGrupos: List<GrupoWhatsappEntity>,
    onCarregarFazendasEGrupos: (String) -> Unit,
    onSalvarConsulta: (ConsultaEntity) -> Unit,
    onVoltar: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var etapaAtual by remember { mutableIntStateOf(1) }
    var salvoOffline by remember { mutableStateOf(false) }
    var consultaSalvaParaResumo by remember { mutableStateOf<ConsultaEntity?>(null) }
    var erroGeral by remember { mutableStateOf<String?>(null) }
    var errosValidacao by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // --- ETAPA 1: Identificação ---
    var selectedClienteId by remember { mutableStateOf<String?>(null) }
    var selectedFazendaId by remember { mutableStateOf<String?>(null) }
    var selectedFrigorificoId by remember { mutableStateOf<String?>(null) }
    var selectedCategoriaId by remember { mutableStateOf<String?>(null) }
    var selectedGruposIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var dataAbate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    // --- ETAPA 2: Pesos ---
    var qtdAnimaisTexto by remember { mutableStateOf("") }
    var sexoLote by remember { mutableStateOf("MACHO") }
    var pesoVivoTexto by remember { mutableStateOf("") }
    var pesoTotalLoteTexto by remember { mutableStateOf("") }
    var pesoCarcacaTexto by remember { mutableStateOf("") }
    var modoEntrada by remember { mutableStateOf("TOTAL") } // "TOTAL" ou "MEDIA"

    // --- ETAPA 3: Maturidade ---
    var d0Texto by remember { mutableStateOf("0") }
    var d2Texto by remember { mutableStateOf("0") }
    var d4Texto by remember { mutableStateOf("0") }
    var d6Texto by remember { mutableStateOf("0") }
    var d8Texto by remember { mutableStateOf("0") }

    // --- ETAPA 4: Acabamento ---
    var g1Texto by remember { mutableStateOf("0") }
    var g2Texto by remember { mutableStateOf("0") }
    var g3Texto by remember { mutableStateOf("0") }
    var g4Texto by remember { mutableStateOf("0") }
    var g5Texto by remember { mutableStateOf("0") }

    // --- ETAPA 5: Adicionais ---
    var penalizacoes by remember { mutableStateOf("") }
    var dif by remember { mutableStateOf("") }
    var escoreRuminal by remember { mutableStateOf("") }
    var perdaVacinalTexto by remember { mutableStateOf("") }
    var contaminacaoGiTexto by remember { mutableStateOf("") }
    var hematomasTexto by remember { mutableStateOf("") }
    var bezerrosPTexto by remember { mutableStateOf("0") }
    var bezerrosMTexto by remember { mutableStateOf("0") }
    var bezerrosGTexto by remember { mutableStateOf("0") }
    var deslocamento by remember { mutableStateOf(false) }
    var relatorioAbate by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val repo = remember(context) { ConsultaRepository(context) }

    fun validarEtapaAtual(): Boolean {
        val novosErros = mutableMapOf<String, String>()

        when (etapaAtual) {
            1 -> {
                if (selectedClienteId.isNullOrBlank()) {
                    novosErros["clienteId"] = "Selecione o cliente"
                }
                if (selectedFazendaId.isNullOrBlank()) {
                    novosErros["fazendaId"] = "Selecione a fazenda"
                }
                if (selectedGruposIds.isEmpty()) {
                    novosErros["grupoWhatsappId"] = "Selecione ao menos um grupo de envio"
                }
                if (selectedFrigorificoId.isNullOrBlank()) {
                    novosErros["frigorificoId"] = "Selecione o frigorífico"
                }
                if (selectedCategoriaId.isNullOrBlank()) {
                    novosErros["categoriaAnimalId"] = "Selecione a categoria"
                }
                if (dataAbate.isBlank()) {
                    novosErros["dataAbate"] = "Informe a data do abate"
                }
            }

            2 -> {
                val qtd = qtdAnimaisTexto.toIntOrNull() ?: 0
                if (qtd <= 0) {
                    novosErros["qtdAnimais"] = "Informe a quantidade de cabeças"
                }
                if (sexoLote.isBlank()) {
                    novosErros["sexoLote"] = "Selecione o sexo do lote"
                }

                val pVivo = pesoVivoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
                if (pVivo <= 0.0) {
                    novosErros["pesoVivoKg"] = "Informe o peso vivo"
                }

                val pTotal = pesoTotalLoteTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
                if (pTotal <= 0.0) {
                    novosErros["pesoTotalLoteKg"] = "Informe o peso total do lote"
                }

                val pCarcaca = pesoCarcacaTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
                if (pCarcaca <= 0.0) {
                    novosErros["pesoCarcacaKg"] = "Informe o peso da carcaça"
                }

                // Trava biológica idêntica ao Zod do backend
                if (pVivo > 0.0 && pCarcaca > 0.0) {
                    val rc = (pCarcaca / pVivo) * 100.0
                    if (rc >= 65.0) {
                        novosErros["pesoCarcacaKg"] = "O Peso da Carcaça está alto demais em relação ao Peso Vivo. Você digitou o Peso Total no campo de Média?"
                    }
                }
            }
        }

        errosValidacao = novosErros
        if (novosErros.isNotEmpty()) {
            erroGeral = "Existem campos obrigatórios não preenchidos ou com inconsistência nesta etapa."
            return false
        }

        erroGeral = null
        return true
    }

    fun avancar() {
        if (validarEtapaAtual()) {
            if (etapaAtual < 5) {
                etapaAtual++
                coroutineScope.launch {
                    scrollState.scrollTo(0)
                }
            }
        }
    }

    fun voltar() {
        if (etapaAtual > 1) {
            etapaAtual--
            erroGeral = null
            errosValidacao = emptyMap()
            coroutineScope.launch {
                scrollState.scrollTo(0)
            }
        }
    }

    fun iniciarNovaConsulta() {
        // Reseta valores de produção mantendo a identificação se desejado
        qtdAnimaisTexto = ""
        pesoVivoTexto = ""
        pesoTotalLoteTexto = ""
        pesoCarcacaTexto = ""
        d0Texto = "0"
        d2Texto = "0"
        d4Texto = "0"
        d6Texto = "0"
        d8Texto = "0"
        g1Texto = "0"
        g2Texto = "0"
        g3Texto = "0"
        g4Texto = "0"
        g5Texto = "0"
        penalizacoes = ""
        dif = ""
        escoreRuminal = ""
        perdaVacinalTexto = ""
        contaminacaoGiTexto = ""
        hematomasTexto = ""
        bezerrosPTexto = "0"
        bezerrosMTexto = "0"
        bezerrosGTexto = "0"
        deslocamento = false
        relatorioAbate = false

        consultaSalvaParaResumo = null
        salvoOffline = false
        erroGeral = null
        errosValidacao = emptyMap()
        etapaAtual = 1

        coroutineScope.launch {
            scrollState.scrollTo(0)
        }
    }

    fun finalizarEGravar() {
        if (!validarEtapaAtual()) return

        val qtd = qtdAnimaisTexto.toIntOrNull() ?: 0
        val pVivo = pesoVivoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
        val pTotal = pesoTotalLoteTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
        val pCarcaca = pesoCarcacaTexto.replace(",", ".").toDoubleOrNull() ?: 0.0

        val d0 = d0Texto.toIntOrNull() ?: 0
        val d2 = d2Texto.toIntOrNull() ?: 0
        val d4 = d4Texto.toIntOrNull() ?: 0
        val d6 = d6Texto.toIntOrNull() ?: 0
        val d8 = d8Texto.toIntOrNull() ?: 0

        val g1 = g1Texto.toIntOrNull() ?: 0
        val g2 = g2Texto.toIntOrNull() ?: 0
        val g3 = g3Texto.toIntOrNull() ?: 0
        val g4 = g4Texto.toIntOrNull() ?: 0
        val g5 = g5Texto.toIntOrNull() ?: 0

        val metricas = repo.calcularMetricas(qtd, pTotal, pVivo, d0, d2, d4, d6, d8, g1, g2, g3, g4, g5)

        val cliente = clientes.find { it.id == selectedClienteId }
        val fazenda = fazendasDoCliente.find { it.id == selectedFazendaId }
        val frigorifico = frigorificos.find { it.id == selectedFrigorificoId }
        val categoria = categorias.find { it.id == selectedCategoriaId }

        val entidade = ConsultaEntity(
            localId = UUID.randomUUID().toString(),
            clienteId = selectedClienteId ?: "",
            clienteNome = cliente?.nomeProdutor ?: "Cliente",
            fazendaId = selectedFazendaId ?: "",
            fazendaNome = fazenda?.nome ?: "Fazenda",
            frigorificoId = selectedFrigorificoId ?: "",
            frigorificoNome = frigorifico?.nome ?: "Frigorífico",
            categoriaAnimalId = selectedCategoriaId ?: "",
            categoriaNome = categoria?.nome ?: "Bovino",
            dataAbate = dataAbate,
            gruposWhatsappIds = selectedGruposIds.joinToString(","),
            qtdAnimais = qtd,
            sexoLote = sexoLote,
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
            perdaVacinalKg = perdaVacinalTexto.replace(",", ".").toDoubleOrNull(),
            contaminacaoGiKg = contaminacaoGiTexto.replace(",", ".").toDoubleOrNull(),
            hematomasKg = hematomasTexto.replace(",", ".").toDoubleOrNull(),
            bezerrosP = bezerrosPTexto.toIntOrNull() ?: 0,
            bezerrosM = bezerrosMTexto.toIntOrNull() ?: 0,
            bezerrosG = bezerrosGTexto.toIntOrNull() ?: 0,
            deslocamento = deslocamento,
            relatorioAbate = relatorioAbate,
            statusEnvio = StatusEnvio.PENDENTE_ENVIO
        )

        onSalvarConsulta(entidade)
        consultaSalvaParaResumo = entidade
        salvoOffline = true
        etapaAtual = 6

        coroutineScope.launch {
            scrollState.scrollTo(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (etapaAtual == 6) "Consulta Finalizada" else "Nova Consulta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = if (etapaAtual == 6) onVoltar else if (etapaAtual > 1) { { voltar() } } else onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceLight,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (etapaAtual == 6) {
                        Button(
                            onClick = { iniciarNovaConsulta() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("Nova Consulta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (etapaAtual > 1) {
                                OutlinedButton(
                                    onClick = { voltar() },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Text("Voltar", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                }
                            }

                            if (etapaAtual < 5) {
                                Button(
                                    onClick = { avancar() },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PadronizaRed),
                                    modifier = Modifier
                                        .weight(if (etapaAtual > 1) 1.2f else 1f)
                                        .height(52.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("Próxima Etapa", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { finalizarEGravar() },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier
                                        .weight(if (etapaAtual > 1) 1.2f else 1f)
                                        .height(52.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null)
                                        Text("Salvar Consulta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Barra de Progresso Visual
            FormProgress(etapaAtual = etapaAtual, totalEtapas = 6)

            // Mensagem de erro de validação da etapa atual
            if (!erroGeral.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = erroGeral!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Exibição da etapa atual com transição suave
            when (etapaAtual) {
                1 -> {
                    Etapa1IdentificacaoView(
                        clientes = clientes,
                        frigorificos = frigorificos,
                        categorias = categorias,
                        fazendasDoCliente = fazendasDoCliente,
                        todosOsGrupos = todosOsGrupos,
                        selectedClienteId = selectedClienteId,
                        selectedFazendaId = selectedFazendaId,
                        selectedFrigorificoId = selectedFrigorificoId,
                        selectedCategoriaId = selectedCategoriaId,
                        selectedGruposIds = selectedGruposIds,
                        dataAbate = dataAbate,
                        erros = errosValidacao,
                        onClienteChanged = { novoClienteId ->
                            selectedClienteId = novoClienteId
                            selectedFazendaId = null
                            onCarregarFazendasEGrupos(novoClienteId)

                            // Regra do Web: Auto-seleção do primeiro grupo do cliente
                            val gruposDoNovoCliente = todosOsGrupos.filter { it.clienteId == novoClienteId }
                            if (gruposDoNovoCliente.isNotEmpty()) {
                                selectedGruposIds = listOf(gruposDoNovoCliente.first().id)
                            } else {
                                selectedGruposIds = emptyList()
                            }
                        },
                        onFazendaChanged = { selectedFazendaId = it },
                        onFrigorificoChanged = { selectedFrigorificoId = it },
                        onCategoriaChanged = { selectedCategoriaId = it },
                        onGruposChanged = { selectedGruposIds = it },
                        onDataAbateChanged = { dataAbate = it }
                    )
                }

                2 -> {
                    Etapa2PesoView(
                        qtdAnimaisTexto = qtdAnimaisTexto,
                        sexoLote = sexoLote,
                        pesoVivoTexto = pesoVivoTexto,
                        pesoTotalLoteTexto = pesoTotalLoteTexto,
                        pesoCarcacaTexto = pesoCarcacaTexto,
                        modoEntrada = modoEntrada,
                        erros = errosValidacao,
                        onQtdAnimaisChanged = { qtdAnimaisTexto = it },
                        onSexoLoteChanged = { sexoLote = it },
                        onPesoVivoChanged = { pesoVivoTexto = it },
                        onPesoTotalLoteChanged = { pesoTotalLoteTexto = it },
                        onPesoCarcacaChanged = { pesoCarcacaTexto = it },
                        onModoEntradaChanged = { modoEntrada = it }
                    )
                }

                3 -> {
                    val qtdTotalEsperada = qtdAnimaisTexto.toIntOrNull() ?: 0
                    Etapa3MaturidadeView(
                        qtdTotalEsperada = qtdTotalEsperada,
                        d0Texto = d0Texto,
                        d2Texto = d2Texto,
                        d4Texto = d4Texto,
                        d6Texto = d6Texto,
                        d8Texto = d8Texto,
                        onD0Changed = { d0Texto = it },
                        onD2Changed = { d2Texto = it },
                        onD4Changed = { d4Texto = it },
                        onD6Changed = { d6Texto = it },
                        onD8Changed = { d8Texto = it }
                    )
                }

                4 -> {
                    val qtdTotalEsperada = qtdAnimaisTexto.toIntOrNull() ?: 0
                    Etapa4AcabamentoView(
                        qtdTotalEsperada = qtdTotalEsperada,
                        g1Texto = g1Texto,
                        g2Texto = g2Texto,
                        g3Texto = g3Texto,
                        g4Texto = g4Texto,
                        g5Texto = g5Texto,
                        onG1Changed = { g1Texto = it },
                        onG2Changed = { g2Texto = it },
                        onG3Changed = { g3Texto = it },
                        onG4Changed = { g4Texto = it },
                        onG5Changed = { g5Texto = it }
                    )
                }

                5 -> {
                    Etapa5AdicionaisView(
                        penalizacoes = penalizacoes,
                        dif = dif,
                        escoreRuminal = escoreRuminal,
                        perdaVacinalTexto = perdaVacinalTexto,
                        contaminacaoGiTexto = contaminacaoGiTexto,
                        hematomasTexto = hematomasTexto,
                        bezerrosPTexto = bezerrosPTexto,
                        bezerrosMTexto = bezerrosMTexto,
                        bezerrosGTexto = bezerrosGTexto,
                        deslocamento = deslocamento,
                        relatorioAbate = relatorioAbate,
                        onPenalizacoesChanged = { penalizacoes = it },
                        onDifChanged = { dif = it },
                        onEscoreRuminalChanged = { escoreRuminal = it },
                        onPerdaVacinalChanged = { perdaVacinalTexto = it },
                        onContaminacaoGiChanged = { contaminacaoGiTexto = it },
                        onHematomasChanged = { hematomasTexto = it },
                        onBezerrosPChanged = { bezerrosPTexto = it },
                        onBezerrosMChanged = { bezerrosMTexto = it },
                        onBezerrosGChanged = { bezerrosGTexto = it },
                        onDeslocamentoChanged = { deslocamento = it },
                        onRelatorioAbateChanged = { relatorioAbate = it }
                    )
                }

                6 -> {
                    consultaSalvaParaResumo?.let { consulta ->
                        Etapa6ConfirmacaoView(
                            consulta = consulta,
                            salvoOffline = salvoOffline
                        )
                    }
                }
            }
        }
    }
}
