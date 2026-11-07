package com.padroniza.app.ui.consulta.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.ui.consulta.components.InputNumericoField
import com.padroniza.app.ui.theme.*

@Composable
fun Etapa4AcabamentoView(
    qtdTotalEsperada: Int,
    g1Texto: String,
    g2Texto: String,
    g3Texto: String,
    g4Texto: String,
    g5Texto: String,
    onG1Changed: (String) -> Unit,
    onG2Changed: (String) -> Unit,
    onG3Changed: (String) -> Unit,
    onG4Changed: (String) -> Unit,
    onG5Changed: (String) -> Unit
) {
    val g1 = g1Texto.toIntOrNull() ?: 0
    val g2 = g2Texto.toIntOrNull() ?: 0
    val g3 = g3Texto.toIntOrNull() ?: 0
    val g4 = g4Texto.toIntOrNull() ?: 0
    val g5 = g5Texto.toIntOrNull() ?: 0

    val totalInformado = g1 + g2 + g3 + g4 + g5
    val isDivergente = qtdTotalEsperada > 0 && totalInformado != qtdTotalEsperada

    val cardBg by animateColorAsState(targetValue = if (isDivergente) WarningBg else SuccessBg, label = "cardBg")
    val cardBorder by animateColorAsState(targetValue = if (isDivergente) WarningBorder else SuccessBorder, label = "cardBorder")
    val cardTextColor by animateColorAsState(targetValue = if (isDivergente) WarningText else SuccessText, label = "cardTextColor")

    val progress = if (qtdTotalEsperada > 0) (totalInformado.toFloat() / qtdTotalEsperada.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "ACABAMENTO DE GORDURA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Distribua as $qtdTotalEsperada carcaças conforme a classificação do frigorífico.",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Grid 2 Colunas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "1 — Ausente",
                    value = g1Texto,
                    onValueChange = onG1Changed,
                    suffix = "carc"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "2 — Escasso",
                    value = g2Texto,
                    onValueChange = onG2Changed,
                    suffix = "carc"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "3 — Mediano",
                    value = g3Texto,
                    onValueChange = onG3Changed,
                    suffix = "carc"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "4 — Uniforme",
                    value = g4Texto,
                    onValueChange = onG4Changed,
                    suffix = "carc"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "5 — Excessivo",
                    value = g5Texto,
                    onValueChange = onG5Changed,
                    suffix = "carc"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Card de Conferência
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardBg)
                .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDivergente) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = cardTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "CONTAGEM DE GORDURA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = cardTextColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "$totalInformado / $qtdTotalEsperada",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = cardTextColor
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.Black.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(cardTextColor)
                    )
                }

                if (isDivergente) {
                    Text(
                        text = "⚠️ O total deve coincidir com os $qtdTotalEsperada animais do lote",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = cardTextColor,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
