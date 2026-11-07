package com.padroniza.app.ui.consulta.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary

@Composable
fun FormProgress(
    etapaAtual: Int,
    totalEtapas: Int = 6,
    titulos: List<String> = listOf("Identificação", "Pesos", "Maturidade", "Gordura", "Adicionais", "Confirmação")
) {
    val progressoPercentual = (etapaAtual.toFloat() / totalEtapas.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressoPercentual, label = "progress")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tituloAtual = titulos.getOrNull(etapaAtual - 1) ?: "Etapa $etapaAtual"
            Text(
                text = "ETAPA $etapaAtual DE $totalEtapas: ${tituloAtual.uppercase()}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (etapaAtual == totalEtapas) Color(0xFF15803D) else PadronizaRed,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${(progressoPercentual * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        // Barra de progresso visual contínua
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BorderLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (etapaAtual == totalEtapas) Color(0xFF10B981) else PadronizaRed)
            )
        }

        // Indicadores dos steps
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 1..totalEtapas) {
                val isDone = step < etapaAtual
                val isCurrent = step == etapaAtual

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone -> PadronizaRed
                                isCurrent -> PadronizaRed.copy(alpha = 0.2f)
                                else -> BorderLight
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PadronizaRed)
                        )
                    } else if (isDone) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "$step",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
