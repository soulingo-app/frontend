package com.soulingo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.soulingo.app.ui.theme.SouLingoColors

@Composable
fun SouLingoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SouLingoColors.Surface,
                shape = RoundedCornerShape(16.dp)
            ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage, color = SouLingoColors.Error)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SouLingoColors.LuminousMint,
            unfocusedBorderColor = SouLingoColors.Neutral.copy(alpha = 0.3f),
            focusedLabelColor = SouLingoColors.LuminousMint,
            unfocusedLabelColor = SouLingoColors.TextSecondary,
            cursorColor = SouLingoColors.LuminousMint
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}