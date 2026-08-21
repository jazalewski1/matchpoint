package dev.jazalewski1.matchpoint.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = "",
) {
    val backgroundModifier =
        if (enabled) {
            val brush =
                Brush.horizontalGradient(
                    colors =
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                        )
                )
            Modifier.background(brush = brush, shape = ButtonDefaults.shape)
        } else {
            Modifier.background(color = MaterialTheme.colorScheme.background)
        }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        modifier =
            modifier
                .then(backgroundModifier)
                .semantics(properties = { this.contentDescription = contentDescription }),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.width(360.dp).padding(12.dp),
            ) {
                PrimaryButton(
                    text = "Press here",
                    onClick = {},
                    enabled = true,
                    contentDescription = "Does something",
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDisabled() {
    AppTheme {
        Surface {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.width(360.dp).padding(12.dp),
            ) {
                PrimaryButton(
                    text = "Press here",
                    onClick = {},
                    enabled = false,
                    contentDescription = "Does something",
                )
            }
        }
    }
}
