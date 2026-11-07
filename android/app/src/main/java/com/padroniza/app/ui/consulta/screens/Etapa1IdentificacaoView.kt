package com.padroniza.app.ui.consulta.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Cliente / Produtor
        DropdownSelectField(
            label = "Cliente / Produtor *",
            selectedValue = selectedClienteId,
            placeholder = "Selecione o cliente",
            options = clientes.map {
                DropdownOption(value = it.id, label = it.nomeProdutor, sublabel = "Doc: ${it.documento}")
            },
            onSelect = { novoClienteId ->
                onClienteChanged(novoClienteId)
            },
            error = erros["clienteId"],
            dialogTitle = "Selecionar Produtor"
        )

        // Fazenda Origem (depende do cliente)
        if (clienteSelecionado != null) {
            DropdownSelectField(
                label = "Fazenda Origem *",
                selectedValue = selectedFazendaId,
                placeholder = if (fazendasDoCliente.isEmpty()) "Nenhuma fazenda cadastrada" else "Selecione a fazenda",
                options = fazendasDoCliente.map {
                    DropdownOption(
                        value = it.id,
                        label = it.nome,
                        sublabel = "${it.cidadeNome}/${it.estadoUf}".ifBlank { null }
                    )
                },
                onSelect = onFazendaChanged,
                error = erros["fazendaId"],
                enabled = fazendasDoCliente.isNotEmpty(),
                dialogTitle = "Selecionar Fazenda"
            )

            // Grupos de Notificação (WhatsApp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "GRUPOS DE NOTIFICAÇÃO (WHATSAPP) *",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                if (cardsGruposParaExibir.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceLight)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Nenhum grupo de WhatsApp selecionado",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    cardsGruposParaExibir.forEach { (grupo, dono) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PadronizaRed.copy(alpha = 0.05f))
                                .border(1.dp, PadronizaRed.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = grupo.nomeGrupo,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Produtor: $dono",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onGruposChanged(selectedGruposIds.filter { it != grupo.id })
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover grupo",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                // Dropdown para incluir grupos de parcerias de outros clientes
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

        // Frigorífico
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

        // Categoria e Data do Abate em Linha 2 Colunas
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
                        onValueChange = onDataAbateChanged,
                        singleLine = true,
                        placeholder = { Text("AAAA-MM-DD") },
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
    }
}
