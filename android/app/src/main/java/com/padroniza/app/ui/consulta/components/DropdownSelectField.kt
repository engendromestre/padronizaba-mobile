package com.padroniza.app.ui.consulta.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.SurfaceLight
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary

data class DropdownOption(
    val value: String,
    val label: String,
    val sublabel: String? = null
)

@Composable
fun DropdownSelectField(
    label: String,
    selectedValue: String?,
    options: List<DropdownOption>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Selecione...",
    error: String? = null,
    enabled: Boolean = true,
    dialogTitle: String = label
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.value == selectedValue }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceLight)
                .border(
                    width = 1.dp,
                    color = if (error != null) MaterialTheme.colorScheme.error else BorderLight,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = enabled) { showDialog = true }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedOption != null) {
                    Column {
                        Text(
                            text = selectedOption.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        if (!selectedOption.sublabel.isNullOrBlank()) {
                            Text(
                                text = selectedOption.sublabel,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir seleção",
                    tint = TextSecondary
                )
            }
        }

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dialogTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { showDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (options.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nenhum item disponível", color = TextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(options) { opt ->
                                val isSelected = opt.value == selectedValue
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PadronizaRed.copy(alpha = 0.08f) else Color.Transparent)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) PadronizaRed else BorderLight.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onSelect(opt.value)
                                            showDialog = false
                                        }
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = opt.label,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PadronizaRed else TextPrimary
                                        )
                                        if (!opt.sublabel.isNullOrBlank()) {
                                            Text(
                                                text = opt.sublabel,
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
