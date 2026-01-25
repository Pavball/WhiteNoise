package pavball.hr.whitenoise.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_cloudy
import whitenoise.composeapp.generated.resources.logo

// import whitenoise.composeapp.generated.resources.ic_logo // <--- UNCOMMENT AND USE YOUR LOGO RESOURCE NAME

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit // Callback to navigate to Home/Onboarding
) {
    val quickSandFont = getCustomQuicksandFontFamily()

    // Animation States
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    // Start Animation & Timer
    LaunchedEffect(key1 = true) {
        // 1. Animate Logo In
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        // 2. Wait for a moment (2 seconds total splash time)
        delay(1500)

        // 3. Navigate away
        onSplashFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. Background Layer ---
        Image(
            painter = painterResource(Res.drawable.bg_cloudy),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        // --- 2. Content Layer ---
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text: DOZZZE
            Text(
                text = "DOZZZE",
                fontFamily = quickSandFont,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal, // Looking at image, it looks clean/thin
                color = LightGray,
                letterSpacing = 4.sp, // Spacing out the letters for style
                modifier = Modifier
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Icon with Glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(300.dp) // Container size for the glow
                    .scale(scale.value)
                    .alpha(alpha.value)
            ) {
                // A. The White Glow (Radial Gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.White.copy(alpha = 0.55f), // Very bright center
                                    0.4f to Color.White.copy(alpha = 0.55f),  // Still bright 40% out (around text)
                                    0.7f to Color.White.copy(alpha = 0.55f),
                                    0.8f to Color.White.copy(alpha = 0.55f),
                                    0.85f to Color.White.copy(alpha = 0.45f),
                                    0.87f to Color.White.copy(alpha = 0.35f),
                                    0.92f to Color.White.copy(alpha = 0.25f),   // Starts fading rapidly
                                    0.95f to Color.White.copy(alpha = 0.15f),
                                    0.97f to Color.White.copy(alpha = 0.10f),
                                    1.0f to Color.Transparent                // Completely invisible at edge
                                )
                            ),
                            shape = CircleShape
                        )
                )


                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    modifier = modifier.size(220.dp)
                )


            }
        }
    }
}