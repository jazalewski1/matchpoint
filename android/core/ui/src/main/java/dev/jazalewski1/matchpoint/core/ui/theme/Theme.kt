package dev.jazalewski1.matchpoint.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val lightScheme =
    lightColorScheme(
        primary = AppColors.Primary.dark,
        onPrimary = AppColors.Others.white,
        primaryContainer = AppColors.Primary.light,
        onPrimaryContainer = AppColors.Primary.veryDark,
        secondary = AppColors.Secondary.dark,
        onSecondary = AppColors.Others.white,
        secondaryContainer = AppColors.Secondary.light,
        onSecondaryContainer = AppColors.Secondary.veryDark,
        tertiary = AppColors.Tertiary.dark,
        onTertiary = AppColors.Others.white,
        tertiaryContainer = AppColors.Tertiary.light,
        onTertiaryContainer = AppColors.Tertiary.veryDark,
        error = AppColors.Error.dark,
        onError = AppColors.Others.white,
        errorContainer = AppColors.Error.light,
        onErrorContainer = AppColors.Error.veryDark,
        background = AppColors.Background.bg,
        onBackground = AppColors.Background.fg,
        surface = AppColors.Others.surface,
        onSurface = AppColors.Others.onSurface,
        surfaceVariant = AppColors.Others.surfaceVariant,
        onSurfaceVariant = AppColors.Others.onSurfaceVariant,
        outline = AppColors.Others.outline,
        outlineVariant = AppColors.Others.outlineVariant,
        scrim = AppColors.Others.scrim,
        inverseSurface = AppColors.Others.inverseSurface,
        inverseOnSurface = AppColors.Others.inverseOnSurface,
        inversePrimary = AppColors.Others.inversePrimary,
        surfaceDim = AppColors.Others.surfaceDim,
        surfaceBright = AppColors.Others.surfaceBright,
        surfaceContainerLowest = AppColors.Others.surfaceContainerLowest,
        surfaceContainerLow = AppColors.Others.surfaceContainerLow,
        surfaceContainer = AppColors.Others.surfaceContainer,
        surfaceContainerHigh = AppColors.Others.surfaceContainerHigh,
        surfaceContainerHighest = AppColors.Others.surfaceContainerHighest,
    )

@Composable
fun AppTheme(
    // darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = lightScheme, typography = AppTypography, content = content)
}
