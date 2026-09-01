package dev.jazalewski1.matchpoint.core.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    object Primary {
        val veryDark = Color(0xFF07023A)
        val dark = Color(0xFF2814E2)
        val mid = Color(0xFF796DEA)
        val light = Color(0xFFC9C5F1)
    }

    object Secondary {
        val veryDark = Color(0xFF100328)
        val dark = Color(0xFF6414E3)
        val mid = Color(0xFF9763EA)
        val light = Color(0xFFCAB1F1)
    }

    object Tertiary {
        val veryDark = Color(0xFF130702)
        val dark = Color(0xFFE35113)
        val mid = Color(0xFFF7956B)
        val light = Color(0xFFEEBFAB)
    }

    object Error {
        val veryDark = Color(0xFF691515)
        val dark = Color(0xFF940F0F)
        val light = Color(0xFFC28383)
    }

    object Background {
        val bg = Others.white
        val fg = Color(0xFF1A1B21)
    }

    object Others {
        val white = Color(0xFFF4F7FA)

        val surface = Color(0xFFFBF8FF)
        val onSurface = Color(0xFF1A1B21)
        val surfaceVariant = Color(0xFFE2E1EC)
        val onSurfaceVariant = Color(0xFF6C6C75)

        val outline = Color(0xFF767680)
        val outlineVariant = Color(0xFFC6C5D0)

        val scrim = Color(0xFF000000)

        val inverseSurface = Color(0xFF2F3036)
        val inverseOnSurface = Color(0xFFF2F0F7)
        val inversePrimary = Color(0xFFB8C4FF)

        val surfaceDim = Color(0xFFDBD9E0)
        val surfaceBright = Color(0xFFFBF8FF)
        val surfaceContainerLowest = Color(0xFFFFFFFF)
        val surfaceContainerLow = Color(0xFFF4F2FA)
        val surfaceContainer = Color(0xFFEFEDF4)
        val surfaceContainerHigh = Color(0xFFE9E7EF)
        val surfaceContainerHighest = Color(0xFFE3E1E9)
    }
}
