package com.padroniza.app.ui.consulta.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.padroniza.app.ui.theme.BorderLight
import com.padroniza.app.ui.theme.PadronizaRed
import com.padroniza.app.ui.theme.TextPrimary
import com.padroniza.app.ui.theme.TextSecondary

@Composable
fun InputNumericoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    placeholder: String = "0",
    error: String? = null,
    disabled: Boolean = false,
    isDecimal: Boolean = false
) {
    Column(
        modifier = modifier.then(if (disabled) Modifier.alpha(0.55f) else Modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = !disabled,
            singleLine = true,
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
            trailingIcon = suffix?.let {
                {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
            ),
            isError = error != null,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PadronizaRed,
                unfocusedBorderColor = BorderLight,
                disabledBorderColor = BorderLight.copy(alpha = 0.6f),
                disabledTextColor = TextPrimary.copy(alpha = 0.7f),
                cursorColor = PadronizaRed
            ),
            modifier = Modifier.fillMaxWidth()
        )

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
}
