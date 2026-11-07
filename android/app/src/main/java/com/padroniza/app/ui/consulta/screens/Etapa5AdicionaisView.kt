package com.padroniza.app.ui.consulta.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.ui.consulta.components.CheckboxCard
import com.padroniza.app.ui.consulta.components.InputNumericoField
import com.padroniza.app.ui.theme.BackgroundLight
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.SurfaceLight
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary

@Composable
fun Etapa5AdicionaisView(
    penalizacoes: String,
    dif: String,
    escoreRuminal: String,
    perdaVacinalTexto: String,
    contaminacaoGiTexto: String,
    hematomasTexto: String,
    bezerrosPTexto: String,
    bezerrosMTexto: String,
    bezerrosGTexto: String,
    deslocamento: Boolean,
    relatorioAbate: Boolean,
    onPenalizacoesChanged: (String) -> Unit,
    onDifChanged: (String) -> Unit,
    onEscoreRuminalChanged: (String) -> Unit,
    onPerdaVacinalChanged: (String) -> Unit,
    onContaminacaoGiChanged: (String) -> Unit,
    onHematomasChanged: (String) -> Unit,
    onBezerrosPChanged: (String) -> Unit,
    onBezerrosMChanged: (String) -> Unit,
    onBezerrosGChanged: (String) -> Unit,
    onDeslocamentoChanged: (Boolean) -> Unit,
    onRelatorioAbateChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // SEÇÃO: Observações Técnicas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceLight)
                .border(1.dp, BorderLight, RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "OBSERVAÇÕES TÉCNICAS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                // Penalizações
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Penalizações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = penalizacoes,
                        onValueChange = onPenalizacoesChanged,
                        placeholder = { Text("Ex: 5 carcaças penalizadas por...") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PadronizaRed,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = PadronizaRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // D.I.F
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("D.I.F (Desvio de Inspeção Federal)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = dif,
                        onValueChange = onDifChanged,
                        placeholder = { Text("Ex: Cisticercose calcificada em 2 carcaças") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PadronizaRed,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = PadronizaRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Escore Ruminal
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Escore Ruminal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = escoreRuminal,
                        onValueChange = onEscoreRuminalChanged,
                        placeholder = { Text("Ex: 3,5 - 4,0") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PadronizaRed,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = PadronizaRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // SEÇÃO: Perdas e Sanidade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceLight)
                .border(1.dp, BorderLight, RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "PERDAS E SANIDADE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                InputNumericoField(
                    label = "Perda Vacinal",
                    value = perdaVacinalTexto,
                    onValueChange = onPerdaVacinalChanged,
                    suffix = "kg/cab",
                    placeholder = "0.00",
                    isDecimal = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Contaminação GI",
                            value = contaminacaoGiTexto,
                            onValueChange = onContaminacaoGiChanged,
                            suffix = "kg total",
                            placeholder = "0.00",
                            isDecimal = true
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Hematomas",
                            value = hematomasTexto,
                            onValueChange = onHematomasChanged,
                            suffix = "kg total",
                            placeholder = "0.00",
                            isDecimal = true
                        )
                    }
                }
            }
        }

        // SEÇÃO: Bezerros (Prenhez)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BackgroundLight)
                .border(1.dp, BorderLight, RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "BEZERROS (PRENHEZ)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Pequeno",
                            value = bezerrosPTexto,
                            onValueChange = onBezerrosPChanged,
                            placeholder = "0"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Médio",
                            value = bezerrosMTexto,
                            onValueChange = onBezerrosMChanged,
                            placeholder = "0"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Grande",
                            value = bezerrosGTexto,
                            onValueChange = onBezerrosGChanged,
                            placeholder = "0"
                        )
                    }
                }
            }
        }

        // Checkboxes em Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CheckboxCard(
                label = "Houve Deslocamento?",
                description = "Viagem do consultor até a unidade",
                checked = deslocamento,
                onCheckedChange = onDeslocamentoChanged
            )

            CheckboxCard(
                label = "Relatório Disponível?",
                description = "Documento oficial do frigorífico",
                checked = relatorioAbate,
                onCheckedChange = onRelatorioAbateChanged
            )
        }
    }
}
