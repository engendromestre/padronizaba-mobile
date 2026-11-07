package com.padroniza.app.ui.consulta.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.ui.theme.*
import java.util.Locale

@Composable
fun Etapa6ConfirmacaoView(
    consulta: ConsultaEntity,
    salvoOffline: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de Sucesso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SuccessBg)
                .border(1.dp, SuccessBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessText,
                    modifier = Modifier.size(24.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Parabéns, sua consulta foi gravada com sucesso.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessText,
                        lineHeight = 18.sp
                    )
                    if (salvoOffline) {
                        Text(
                            text = "Registro salvo com segurança neste aparelho. A sincronização acontece automaticamente quando houver conexão.",
                            fontSize = 12.sp,
                            color = SuccessText.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Seção: Identificação
        SecaoResumo(titulo = "Identificação") {
            LinhaResumo("Cliente / Produtor", consulta.clienteNome)
            LinhaResumo("Fazenda Origem", consulta.fazendaNome)
            LinhaResumo("Grupos de WhatsApp", consulta.gruposWhatsappIds.ifBlank { "—" })
            LinhaResumo("Frigorífico", consulta.frigorificoNome)
            LinhaResumo("Categoria", consulta.categoriaNome)
            LinhaResumo("Data do Abate", formatarDataBr(consulta.dataAbate))
        }

        // Seção: Animais e Peso
        SecaoResumo(titulo = "Animais e Peso") {
            LinhaResumo("Quantidade de Animais", "${consulta.qtdAnimais} cab")
            LinhaResumo("Sexo do Lote", if (consulta.sexoLote == "MACHO") "Machos" else "Fêmeas")
            LinhaResumo("Peso Vivo Médio", "${formatarDecimal(consulta.pesoVivoKg)} kg")
            LinhaResumo("Peso Total (Carcaças)", "${formatarDecimal(consulta.pesoTotalLoteKg)} kg")
            LinhaResumo("Carcaça Média", "${formatarDecimal(consulta.pesoCarcacaKg)} kg")
            LinhaResumo("Média @", "${formatarDecimal(consulta.mediaArroba)} @")
            LinhaResumo("Rendimento (RC)", "${formatarDecimal(consulta.rcPercentual)}%")
        }

        // Seção: Maturidade Dentária
        SecaoResumo(titulo = "Maturidade Dentária") {
            LinhaResumo("0 Dentes", "${consulta.dentes0} cab (${formatarDecimal(consulta.dentes0Pct)}%)")
            LinhaResumo("2 Dentes", "${consulta.dentes2} cab (${formatarDecimal(consulta.dentes2Pct)}%)")
            LinhaResumo("4 Dentes", "${consulta.dentes4} cab (${formatarDecimal(consulta.dentes4Pct)}%)")
            LinhaResumo("6 Dentes", "${consulta.dentes6} cab (${formatarDecimal(consulta.dentes6Pct)}%)")
            LinhaResumo("8 Dentes", "${consulta.dentes8} cab (${formatarDecimal(consulta.dentes8Pct)}%)")
        }

        // Seção: Acabamento de Gordura
        SecaoResumo(titulo = "Acabamento de Gordura") {
            LinhaResumo("1 — Ausente", "${consulta.gordura1} carc (${formatarDecimal(consulta.gordura1Pct)}%)")
            LinhaResumo("2 — Escasso", "${consulta.gordura2} carc (${formatarDecimal(consulta.gordura2Pct)}%)")
            LinhaResumo("3 — Mediano", "${consulta.gordura3} carc (${formatarDecimal(consulta.gordura3Pct)}%)")
            LinhaResumo("4 — Uniforme", "${consulta.gordura4} carc (${formatarDecimal(consulta.gordura4Pct)}%)")
            LinhaResumo("5 — Excessivo", "${consulta.gordura5} carc (${formatarDecimal(consulta.gordura5Pct)}%)")
        }

        // Seção: Informações Adicionais
        SecaoResumo(titulo = "Informações Adicionais") {
            LinhaResumo("Penalizações", consulta.penalizacoes?.ifBlank { null } ?: "—")
            LinhaResumo("D.I.F", consulta.dif?.ifBlank { null } ?: "—")
            LinhaResumo("Escore Ruminal", consulta.escoreRuminal?.ifBlank { null } ?: "—")
            LinhaResumo("Perda Vacinal", consulta.perdaVacinalKg?.let { "${formatarDecimal(it)} kg/cab" } ?: "—")
            LinhaResumo("Contaminação GI", consulta.contaminacaoGiKg?.let { "${formatarDecimal(it)} kg" } ?: "—")
            LinhaResumo("Hematomas", consulta.hematomasKg?.let { "${formatarDecimal(it)} kg" } ?: "—")
            LinhaResumo("Bezerros P / M / G", "${consulta.bezerrosP} / ${consulta.bezerrosM} / ${consulta.bezerrosG}")
            LinhaResumo("Deslocamento", if (consulta.deslocamento) "Sim" else "Não")
            LinhaResumo("Relatório de Abate", if (consulta.relatorioAbate) "Sim" else "Não")
        }
    }
}

@Composable
private fun SecaoResumo(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceLight)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = titulo.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            content()
        }
    }
}

@Composable
private fun LinhaResumo(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

private fun formatarDecimal(valor: Double): String {
    return String.format(Locale.US, "%.2f", valor)
}

private fun formatarDataBr(iso: String): String {
    val partes = iso.split("-")
    if (partes.size == 3) {
        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }
    return iso
}
