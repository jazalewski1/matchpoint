package dev.jazalewski1.matchpoint.feature.match.main.detail

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jazalewski1.matchpoint.core.common.Side
import dev.jazalewski1.matchpoint.core.ui.common.contentDesc
import dev.jazalewski1.matchpoint.core.ui.theme.AppColors
import dev.jazalewski1.matchpoint.core.ui.theme.AppTheme
import dev.jazalewski1.matchpoint.feature.match.R

@Composable
internal fun Indication(
    side: Side,
    onClick: () -> Unit,
    largeText: String,
    smallText: String,
    backgroundColor: Color,
    pulseColor: Color,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedColor = remember { Animatable(backgroundColor) }
    LaunchedEffect(Unit) {
        animatedColor.animateTo(
            targetValue = pulseColor,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = INDICATION_PULSE_HALF_DURATION_MS,
                            easing = EaseOutQuad,
                        ),
                    repeatMode = RepeatMode.Reverse,
                ),
        )
    }
    val colors =
        if (side == Side.LHS) {
            arrayOf(0.0f to animatedColor.value, 0.5f to backgroundColor)
        } else {
            arrayOf(0.5f to backgroundColor, 1.0f to animatedColor.value)
        }
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .drawBehind { drawRect(brush = Brush.horizontalGradient(colorStops = colors)) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = largeText,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = AppColors.Background.bg,
                fontSize = 182.sp,
            )
            Text(
                text = smallText,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = AppColors.Background.bg,
                fontSize = 92.sp,
            )
        }
    }
}

@Composable
internal fun SwitchIndication(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val transition = rememberInfiniteTransition()
    val animationProgress by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(INDICATION_PULSE_HALF_DURATION_MS, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse,
                ),
        )
    val offset = animationProgress * 80
    Box(
        modifier =
            Modifier.fillMaxSize()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource,
                )
                .background(color = AppColors.Others.inverseSurface)
                .contentDesc("Side Switch Indication")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "SWITCH SIDES",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = AppColors.Others.inverseOnSurface,
                fontSize = 108.sp,
            )
            Spacer(Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    tint = AppColors.Others.inverseOnSurface,
                    modifier = Modifier.size(128.dp).offset(x = -offset.dp),
                    contentDescription = null,
                )
                Spacer(Modifier.width(40.dp))
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    tint = AppColors.Others.inverseOnSurface,
                    modifier = Modifier.size(128.dp).offset(x = offset.dp),
                    contentDescription = null,
                )
            }
        }
    }
}

@HorizontalPreview
@Composable
private fun LhsGameIndicationPreview() {
    AppTheme {
        Indication(
            side = Side.LHS,
            largeText = "GAME",
            smallText = "6 : 4",
            onClick = {},
            backgroundColor = AppColors.Secondary.light,
            pulseColor = AppColors.Secondary.mid,
        )
    }
}

@HorizontalPreview
@Composable
private fun RhsGameIndicationPreview() {
    AppTheme {
        Indication(
            side = Side.RHS,
            largeText = "GAME",
            smallText = "6 : 4",
            onClick = {},
            backgroundColor = AppColors.Secondary.light,
            pulseColor = AppColors.Secondary.mid,
        )
    }
}

@HorizontalPreview
@Composable
private fun SwitchIndicationPreview() {
    AppTheme {
        SwitchIndication(onClick = {})
    }
}
