package pavball.hr.whitenoise.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.drawVerticalFadingEdges(
    scrollableState: ScrollableState,
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
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            val topEdgeHeightPx = topEdgeHeight.toPx()
            val bottomEdgeHeightPx = bottomEdgeHeight.toPx()

            if (topAlpha > 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = topAlpha),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = topEdgeHeightPx,
                    ),
                    blendMode = BlendMode.DstOut,
                )
            }

            if (bottomAlpha > 0f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = bottomAlpha)
                        ),
                        startY = size.height - bottomEdgeHeightPx,
                        endY = size.height,
                    ),
                    blendMode = BlendMode.DstOut,
                )
            }
        }
}