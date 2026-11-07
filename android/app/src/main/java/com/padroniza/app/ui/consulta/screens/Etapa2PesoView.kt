package com.padroniza.app.ui.consulta.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
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
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

@Composable
fun Etapa2PesoView(
    qtdAnimaisTexto: String,
    sexoLote: String,
    pesoVivoTexto: String,
    pesoTotalLoteTexto: String,
    pesoCarcacaTexto: String,
    modoEntrada: String, // "TOTAL" ou "MEDIA"
    erros: Map<String, String>,
    onQtdAnimaisChanged: (String) -> Unit,
    onSexoLoteChanged: (String) -> Unit,
    onPesoVivoChanged: (String) -> Unit,
    onPesoTotalLoteChanged: (String) -> Unit,
    onPesoCarcacaChanged: (String) -> Unit,
    onModoEntradaChanged: (String) -> Unit
) {
    val qtd = qtdAnimaisTexto.toIntOrNull() ?: 0
    val pVivo = pesoVivoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
    val pTotal = pesoTotalLoteTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
    val pCarcaca = pesoCarcacaTexto.replace(",", ".").toDoubleOrNull() ?: 0.0

    // Cálculos em tempo real
    val mediaArroba = if (qtd > 0 && pTotal > 0) {
        BigDecimal((pTotal / qtd) / 15.0).setScale(2, RoundingMode.HALF_UP).toDouble()
    } else 0.0

    val rcPercentual = if (qtd > 0 && pTotal > 0 && pVivo > 0) {
        BigDecimal((pTotal / (pVivo * qtd)) * 100.0).setScale(2, RoundingMode.HALF_UP).toDouble()
    } else 0.0

    val travaBiologicaExcedida = if (pVivo > 0 && pCarcaca > 0) {
        ((pCarcaca / pVivo) * 100.0) >= 65.0
    } else false

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // CARD: Tamanho do Lote
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
                    text = "TAMANHO DO LOTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quantidade de Animais
                    Box(modifier = Modifier.weight(1f)) {
                        InputNumericoField(
                            label = "Qtd. de Animais *",
                            value = qtdAnimaisTexto,
                            onValueChange = onQtdAnimaisChanged,
                            suffix = "cab",
                            placeholder = "0",
                            error = erros["qtdAnimais"]
                        )
                    }

                    // Seletor de Sexo Segmentado
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Sexo do Lote *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundLight)
                                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val isMacho = sexoLote == "MACHO"
                            val isFemea = sexoLote == "FEMEA"

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isMacho) SurfaceLight else Color.Transparent)
                                    .clickable { onSexoLoteChanged("MACHO") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Machos",
                                    fontSize = 12.sp,
                                    fontWeight = if (isMacho) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isMacho) PadronizaRed else TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isFemea) SurfaceLight else Color.Transparent)
                                    .clickable { onSexoLoteChanged("FEMEA") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Fêmeas",
                                    fontSize = 12.sp,
                                    fontWeight = if (isFemea) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFemea) PadronizaRed else TextSecondary
                                )
                            }
                        }

                        if (!erros["sexoLote"].isNullOrBlank()) {
                            Text(
                                text = erros["sexoLote"]!!,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                // Badge Dinâmico de Resumo do Lote
                if (qtd > 0 && sexoLote.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(InfoBg)
                            .border(1.dp, InfoBorder, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RESUMO DO LOTE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = InfoText
                            )
                            Text(
                                text = "$qtd ${if (qtd == 1) "animal" else "animais"} (${if (sexoLote == "MACHO") "machos" else "fêmeas"})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = InfoText
                            )
                        }
                    }
                }
            }
        }

        // Peso Vivo Médio
        InputNumericoField(
            label = "Peso Vivo Médio (Fazenda) *",
            value = pesoVivoTexto,
            onValueChange = onPesoVivoChanged,
            suffix = "kg",
            placeholder = "0.00",
            isDecimal = true,
            error = erros["pesoVivoKg"]
        )

        // Seletor de Modo de Entrada de Pesos
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "INFORMAR PESOS POR:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundLight)
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isModoTotal = modoEntrada == "TOTAL"
                val isModoMedia = modoEntrada == "MEDIA"

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isModoTotal) SurfaceLight else Color.Transparent)
                        .clickable { onModoEntradaChanged("TOTAL") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Total do Lote",
                        fontSize = 13.sp,
                        fontWeight = if (isModoTotal) FontWeight.Bold else FontWeight.Medium,
                        color = if (isModoTotal) PadronizaRed else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isModoMedia) SurfaceLight else Color.Transparent)
                        .clickable { onModoEntradaChanged("MEDIA") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Carcaça Média",
                        fontSize = 13.sp,
                        fontWeight = if (isModoMedia) FontWeight.Bold else FontWeight.Medium,
                        color = if (isModoMedia) PadronizaRed else TextSecondary
                    )
                }
            }
        }

        // Inputs de Peso (Com sincronização inteligente bidirecional)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            InputNumericoField(
                label = "Peso Total (Carcaças) *",
                value = pesoTotalLoteTexto,
                onValueChange = { novoTotal ->
                    onPesoTotalLoteChanged(novoTotal)
                    if (modoEntrada == "TOTAL" && qtd > 0) {
                        val num = novoTotal.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (num > 0) {
                            val mediaCalculada = BigDecimal(num / qtd).setScale(2, RoundingMode.HALF_UP).toDouble()
                            onPesoCarcacaChanged(String.format(Locale.US, "%.2f", mediaCalculada))
                        }
                    }
                },
                suffix = "kg",
                placeholder = "0.00",
                isDecimal = true,
                disabled = modoEntrada == "MEDIA",
                error = erros["pesoTotalLoteKg"]
            )

            InputNumericoField(
                label = "Carcaça Média *",
                value = pesoCarcacaTexto,
                onValueChange = { novaMedia ->
                    onPesoCarcacaChanged(novaMedia)
                    if (modoEntrada == "MEDIA" && qtd > 0) {
                        val num = novaMedia.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (num > 0) {
                            val totalCalculado = BigDecimal(num * qtd).setScale(2, RoundingMode.HALF_UP).toDouble()
                            onPesoTotalLoteChanged(String.format(Locale.US, "%.2f", totalCalculado))
                        }
                    }
                },
                suffix = "kg",
                placeholder = "0.00",
                isDecimal = true,
                disabled = modoEntrada == "TOTAL",
                error = erros["pesoCarcacaKg"]
            )
        }

        // Alerta de Trava Biológica (RC >= 65%)
        if (travaBiologicaExcedida) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEF2F2))
                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Erro de validação biológica",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "O Peso da Carcaça está alto demais em relação ao Peso Vivo. Você digitou o Peso Total no campo de Média?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        lineHeight = 16.sp
                    )
                }
            }
        } else if (rcPercentual > 60.0) {
            // Alerta Biológico (RC > 60%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WarningBg)
                    .border(1.dp, WarningBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Atenção ao rendimento",
                        tint = WarningText
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "ATENÇÃO AO RENDIMENTO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = WarningText
                        )
                        Text(
                            text = "RC de $rcPercentual% está acima da média biológica. Verifique se o Peso Vivo e Peso de Carcaça estão corretos.",
                            fontSize = 11.sp,
                            color = WarningText,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Card de Resultados Final (Padroniza Red)
        if (rcPercentual > 0.0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PadronizaRed)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "MÉDIA @",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f", mediaArroba),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "RENDIMENTO (RC)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", rcPercentual)}%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Cálculos baseados no peso de carcaça resfriada.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
