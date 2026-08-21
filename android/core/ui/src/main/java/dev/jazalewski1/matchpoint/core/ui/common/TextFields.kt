package dev.jazalewski1.matchpoint.core.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    contentDescription: String,
    imeAction: ImeAction = ImeAction.Done,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = !isValid,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = imeAction, capitalization = capitalization),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.secondary,
            ),
        shape = RoundedCornerShape(8.dp),
        modifier =
            Modifier.fillMaxWidth()
                .semantics(properties = { this.contentDescription = contentDescription }),
    )
}
