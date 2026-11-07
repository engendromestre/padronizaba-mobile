package com.padroniza.app.ui.consulta.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.SurfaceLight
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary

@Composable
fun CheckboxCard(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (checked) PadronizaRed.copy(alpha = 0.05f) else SurfaceLight)
            .border(
                width = if (checked) 1.5.dp else 1.dp,
                color = if (checked) PadronizaRed else BorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = PadronizaRed,
                    uncheckedColor = BorderLight,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}
