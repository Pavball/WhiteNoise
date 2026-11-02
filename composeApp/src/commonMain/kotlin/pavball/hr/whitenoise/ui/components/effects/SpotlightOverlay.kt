package pavball.hr.whitenoise.ui.components.effects

import androidx.compose.animation.core.animateRectAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.Mint
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.got_it
import whitenoise.composeapp.generated.resources.next
import whitenoise.composeapp.generated.resources.skip

/** Where the elements a [SpotlightOverlay] can point at are on screen, by key. */
@Stable
class SpotlightState {
    internal val targets = mutableStateMapOf<String, Rect>()
}

@Composable
fun rememberSpotlightState(): SpotlightState = remember { SpotlightState() }

/** Lets a [SpotlightOverlay] step with this [key] highlight this element. */
fun Modifier.spotlightTarget(state: SpotlightState, key: String): Modifier =
    onGloballyPositioned { state.targets[key] = it.boundsInWindow() }

/** One tip: the element it points at (see [spotlightTarget]) and what it says. */
data class SpotlightStep(val targetKey: String, val title: String, val text: String)

/**
 * Dims the screen except for the current step's element and explains it in a
 * card beside it. Touches don't reach the screen below while it shows. Place it
 * on top of the content it explains, filling the same area.
 */
@Composable
fun SpotlightOverlay(
    state: SpotlightState,
    steps: List<SpotlightStep>,
    currentStep: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    font: FontFamily,
    modifier: Modifier = Modifier
) {
    val step = steps[currentStep]
    val density = LocalDensity.current
    var origin by remember { mutableStateOf(Offset.Zero) }
    var overlaySize by remember { mutableStateOf(IntSize.Zero) }

    val margin = with(density) { 8.dp.toPx() }
    val target = state.targets[step.targetKey]?.translate(-origin)?.inflate(margin)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                origin = it.positionInWindow()
                overlaySize = it.size
            }
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        // Until both this overlay and the element are laid out there is nothing to point at.
        if (target == null || overlaySize == IntSize.Zero) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = DIM_ALPHA)))
        } else {
            Spotlight(
                target = target,
                overlayHeight = overlaySize.height,
                step = step,
                position = currentStep + 1,
                count = steps.size,
                font = font,
                onNext = onNext,
                onSkip = onSkip
            )
        }
    }
}

@Composable
private fun BoxScope.Spotlight(
    target: Rect,
    overlayHeight: Int,
    step: SpotlightStep,
    position: Int,
    count: Int,
    font: FontFamily,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    // Starts on the element, glides to the next one, and follows it while the screen scrolls.
    val hole by animateRectAsState(target, label = "spotlightHole")

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            // offscreen, so the cleared hole shows the screen instead of black
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        drawRect(Color.Black.copy(alpha = DIM_ALPHA))
        val corner = CornerRadius(20.dp.toPx())
        drawRoundRect(
            color = Color.Black,
            topLeft = hole.topLeft,
            size = hole.size,
            cornerRadius = corner,
            blendMode = BlendMode.Clear
        )
        drawRoundRect(
            color = Mint,
            topLeft = hole.topLeft,
            size = hole.size,
            cornerRadius = corner,
            style = Stroke(width = 2.dp.toPx())
        )
    }

    // The card goes on whichever side of the element has more room.
    val gap = 16.dp
    val cardModifier = with(density) {
        if (hole.center.y < overlayHeight / 2f) {
            val top = hole.bottom.coerceAtLeast(0f).toDp()
            Modifier.align(Alignment.TopCenter).padding(top = top + gap)
        } else {
            val bottom = (overlayHeight - hole.top).coerceAtLeast(0f).toDp()
            Modifier.align(Alignment.BottomCenter).padding(bottom = bottom + gap)
        }
    }
    TipCard(
        step = step,
        position = position,
        count = count,
        font = font,
        onNext = onNext,
        onSkip = onSkip,
        modifier = cardModifier.padding(horizontal = 24.dp)
    )
}

private const val DIM_ALPHA = 0.75f

@Composable
private fun TipCard(
    step: SpotlightStep,
    position: Int,
    count: Int,
    font: FontFamily,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DarkNavy)
            .border(1.dp, Mint.copy(alpha = 0.6f), shape)
            .padding(start = 20.dp, end = 12.dp, top = 18.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = step.title,
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Mint
        )
        Text(
            text = step.text,
            fontFamily = font,
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(end = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (position < count) {
                TextButton(onClick = onSkip) {
                    Text(stringResource(Res.string.skip), fontFamily = font, color = Color.White.copy(alpha = 0.7f))
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "$position / $count",
                fontFamily = font,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = DarkNavy)
            ) {
                Text(
                    text = stringResource(if (position < count) Res.string.next else Res.string.got_it),
                    fontFamily = font,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
