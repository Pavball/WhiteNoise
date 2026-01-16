package pavball.hr.whitenoise.ui.components.effects

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.drawColorFadingEdges(
    scrollableState: ScrollableState,
    edgeColor: Color = Color.Blue,
    topEdgeHeight: Dp = 72.dp,
    bottomEdgeHeight: Dp = 72.dp,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 300)
) = composed {
    val topAlpha by animateFloatAsState(
        targetValue = if (scrollableState.canScrollBackward) 1f else 0f,
        animationSpec = animationSpec,
        label = "TopEdgeAlpha"
    )

    val bottomAlpha by animateFloatAsState(
        targetValue = if (scrollableState.canScrollForward) 1f else 0f,
        animationSpec = animationSpec,
        label = "BottomEdgeAlpha"
    )

    this
        .drawWithContent {
            drawContent()

            val topEdgeHeightPx = topEdgeHeight.toPx()
            val bottomEdgeHeightPx = bottomEdgeHeight.toPx()

            // Draw Top Edge (Color -> Transparent)
            if (topAlpha > 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            // We apply the animated alpha to the color
                            edgeColor.copy(alpha = topAlpha),
                            edgeColor.copy(alpha = 0f)
                        ),
                        startY = 0f,
                        endY = topEdgeHeightPx,
                    )
                )
            }

            // Draw Bottom Edge (Transparent -> Color)
            if (bottomAlpha > 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            edgeColor.copy(alpha = 0f),
                            edgeColor.copy(alpha = bottomAlpha)
                        ),
                        startY = size.height - bottomEdgeHeightPx,
                        endY = size.height,
                    )
                )
            }
        }
}