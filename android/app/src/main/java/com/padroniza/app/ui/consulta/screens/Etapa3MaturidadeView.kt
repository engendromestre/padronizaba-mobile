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
fun Etapa3MaturidadeView(
    qtdTotalEsperada: Int,
    d0Texto: String,
    d2Texto: String,
    d4Texto: String,
    d6Texto: String,
    d8Texto: String,
    onD0Changed: (String) -> Unit,
    onD2Changed: (String) -> Unit,
    onD4Changed: (String) -> Unit,
    onD6Changed: (String) -> Unit,
    onD8Changed: (String) -> Unit
) {
    val d0 = d0Texto.toIntOrNull() ?: 0
    val d2 = d2Texto.toIntOrNull() ?: 0
    val d4 = d4Texto.toIntOrNull() ?: 0
    val d6 = d6Texto.toIntOrNull() ?: 0
    val d8 = d8Texto.toIntOrNull() ?: 0

    val totalInformado = d0 + d2 + d4 + d6 + d8
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
                text = "MATURIDADE DENTÁRIA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Distribua o total de $qtdTotalEsperada animais pelas categorias abaixo.",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Grid 2 Colunas para entrada ágil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "0 Dentes",
                    value = d0Texto,
                    onValueChange = onD0Changed,
                    suffix = "cab"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "2 Dentes",
                    value = d2Texto,
                    onValueChange = onD2Changed,
                    suffix = "cab"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "4 Dentes",
                    value = d4Texto,
                    onValueChange = onD4Changed,
                    suffix = "cab"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "6 Dentes",
                    value = d6Texto,
                    onValueChange = onD6Changed,
                    suffix = "cab"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                InputNumericoField(
                    label = "8 Dentes",
                    value = d8Texto,
                    onValueChange = onD8Changed,
                    suffix = "cab"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Card de Conferência do Lote
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
                            text = "CONFERÊNCIA DO LOTE",
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

                // Barra de progresso visual de conferência
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
                        text = "⚠️ A soma não bate com o total de animais do lote",
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
