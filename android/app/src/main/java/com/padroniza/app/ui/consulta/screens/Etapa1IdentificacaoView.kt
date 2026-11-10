package com.padroniza.app.ui.consulta.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.data.local.entities.CategoriaEntity
import com.padroniza.app.data.local.entities.ClienteEntity
import com.padroniza.app.data.local.entities.FazendaEntity
import com.padroniza.app.data.local.entities.FrigorificoEntity
import com.padroniza.app.data.local.entities.GrupoWhatsappEntity
import com.padroniza.app.ui.consulta.components.DropdownOption
import com.padroniza.app.ui.consulta.components.DropdownSelectField
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.SurfaceLight
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Etapa1IdentificacaoView(
    clientes: List<ClienteEntity>,
    frigorificos: List<FrigorificoEntity>,
    categorias: List<CategoriaEntity>,
    fazendasDoCliente: List<FazendaEntity>,
    todosOsGrupos: List<GrupoWhatsappEntity>,
    selectedClienteId: String?,
    selectedFazendaId: String?,
    selectedFrigorificoId: String?,
    selectedCategoriaId: String?,
    selectedGruposIds: List<String>,
    dataAbate: String,
    erros: Map<String, String>,
    onClienteChanged: (String) -> Unit,
    onFazendaChanged: (String) -> Unit,
    onFrigorificoChanged: (String) -> Unit,
    onCategoriaChanged: (String) -> Unit,
    onGruposChanged: (List<String>) -> Unit,
    onDataAbateChanged: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember(dataAbate) {
        Calendar.getInstance().apply {
            val partes = dataAbate.split("/")
            if (partes.size == 3) {
                val d = partes[0].toIntOrNull()
                val m = partes[1].toIntOrNull()
                val y = partes[2].toIntOrNull()
                if (d != null && m != null && y != null && (m in 1..12)) {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m - 1)
                    set(Calendar.DAY_OF_MONTH, d)
                }
            }
        }
    }

    val datePickerDialog = remember(context, calendar) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                onDataAbateChanged(dataFormatada)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val clienteSelecionado = clientes.find { it.id == selectedClienteId }

    // Mapeia todos os grupos com o nome do produtor proprietário
    val gruposComDono = remember(todosOsGrupos, clientes) {
        todosOsGrupos.map { grp ->
            val dono = clientes.find { it.id == grp.clienteId }?.nomeProdutor ?: "Parceiro"
            grp to dono
        }
    }

    val cardsGruposParaExibir = remember(selectedGruposIds, gruposComDono) {
        gruposComDono.filter { (grp, _) -> selectedGruposIds.contains(grp.id) }
    }

    val gruposDisponiveisParaParceria = remember(selectedGruposIds, gruposComDono) {
        gruposComDono.filter { (grp, _) -> !selectedGruposIds.contains(grp.id) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Cliente / Produtor
        DropdownSelectField(
            label = "Cliente / Produtor *",
            selectedValue = selectedClienteId,
            placeholder = "Selecione o cliente",
            options = clientes.map {
                DropdownOption(value = it.id, label = it.nomeProdutor, sublabel = "Doc: ${it.documento}")
            },
            onSelect = onClienteChanged,
            error = erros["clienteId"],
            dialogTitle = "Selecionar Produtor"
        )

        // 2. Fazenda Origem (sempre no layout; desabilitado enquanto cliente não for escolhido)
        DropdownSelectField(
            label = "Fazenda Origem *",
            selectedValue = selectedFazendaId,
            placeholder = when {
                clienteSelecionado == null -> "Selecione o produtor primeiro"
                fazendasDoCliente.isEmpty() -> "Nenhuma fazenda cadastrada"
                else -> "Selecione a fazenda"
            },
            options = fazendasDoCliente.map {
                DropdownOption(
                    value = it.id,
                    label = it.nome,
                    sublabel = "${it.cidadeNome}/${it.estadoUf}".ifBlank { null }
                )
            },
            onSelect = onFazendaChanged,
            error = erros["fazendaId"],
            enabled = clienteSelecionado != null && fazendasDoCliente.isNotEmpty(),
            dialogTitle = "Selecionar Fazenda"
        )

        // 3. Frigorífico (posição fixa essencial no fluxo)
        DropdownSelectField(
            label = "Frigorífico *",
            selectedValue = selectedFrigorificoId,
            placeholder = "Selecione o frigorífico",
            options = frigorificos.map {
                DropdownOption(
                    value = it.id,
                    label = it.nome,
                    sublabel = "${it.cidadeNome}/${it.estadoUf}"
                )
            },
            onSelect = onFrigorificoChanged,
            error = erros["frigorificoId"],
            dialogTitle = "Selecionar Frigorífico"
        )

        // 4. Categoria e Data do Abate em Linha 2 Colunas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelectField(
                    label = "Categoria *",
                    selectedValue = selectedCategoriaId,
                    placeholder = "Selecione",
                    options = categorias.map {
                        DropdownOption(value = it.id, label = it.nome)
                    },
                    onSelect = onCategoriaChanged,
                    error = erros["categoriaAnimalId"],
                    dialogTitle = "Selecionar Categoria"
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Data do Abate *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = dataAbate,
                        onValueChange = { novoTexto ->
                            if (novoTexto.matches(Regex("""^\d{4}-\d{2}-\d{2}$"""))) {
                                val p = novoTexto.split("-")
                                onDataAbateChanged("${p[2]}/${p[1]}/${p[0]}")
                            } else {
                                val digits = novoTexto.filter { it.isDigit() }.take(8)
                                val isDeleting = novoTexto.length < dataAbate.length
                                val cleanedDigits = if (isDeleting && dataAbate.endsWith("/") && !novoTexto.endsWith("/")) {
                                    digits.dropLast(1)
                                } else {
                                    digits
                                }
                                val formatted = buildString {
                                    for (i in cleanedDigits.indices) {
                                        append(cleanedDigits[i])
                                        if ((i == 1 || i == 3) && i != cleanedDigits.lastIndex) {
                                            append("/")
                                        }
                                    }
                                }
                                onDataAbateChanged(formatted)
                            }
                        },
                        singleLine = true,
                        placeholder = { Text("DD/MM/AAAA") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Selecionar data",
                                    tint = PadronizaRed
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PadronizaRed,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = PadronizaRed
                        ),
                        isError = erros["dataAbate"] != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!erros["dataAbate"].isNullOrBlank()) {
                        Text(
                            text = erros["dataAbate"]!!,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // 5. Grupos de Notificação (WhatsApp) - Seção Compacta com Chips
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "GRUPOS DE WHATSAPP (NOTIFICAÇÃO) *",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            if (cardsGruposParaExibir.isEmpty()) {
                Text(
                    text = if (clienteSelecionado == null) "Selecione o produtor para carregar os grupos" else "Nenhum grupo de WhatsApp selecionado",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cardsGruposParaExibir.forEach { (grupo, _) ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PadronizaRed.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, PadronizaRed.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = grupo.nomeGrupo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = {
                                        onGruposChanged(selectedGruposIds.filter { it != grupo.id })
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remover",
                                        tint = PadronizaRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (gruposDisponiveisParaParceria.isNotEmpty()) {
                DropdownSelectField(
                    label = "",
                    selectedValue = null,
                    placeholder = "+ Incluir grupo de parceria para disparo",
                    options = gruposDisponiveisParaParceria.map { (grp, dono) ->
                        DropdownOption(
                            value = grp.id,
                            label = grp.nomeGrupo,
                            sublabel = "Produtor: $dono"
                        )
                    },
                    onSelect = { grupoAdicionadoId ->
                        if (!selectedGruposIds.contains(grupoAdicionadoId)) {
                            onGruposChanged(selectedGruposIds + grupoAdicionadoId)
                        }
                    },
                    dialogTitle = "Incluir Grupo de Parceria"
                )
            }

            if (!erros["grupoWhatsappId"].isNullOrBlank()) {
                Text(
                    text = erros["grupoWhatsappId"]!!,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
