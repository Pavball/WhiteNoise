package pavball.hr.whitenoise.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.GlowingTimerCircle
import pavball.hr.whitenoise.ui.components.effects.WaveformVisualizer
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_starry
import whitenoise.composeapp.generated.resources.get_started
import whitenoise.composeapp.generated.resources.ic_notes
import whitenoise.composeapp.generated.resources.logo
import whitenoise.composeapp.generated.resources.next
import whitenoise.composeapp.generated.resources.onboarding_journal_text
import whitenoise.composeapp.generated.resources.onboarding_journal_title
import whitenoise.composeapp.generated.resources.onboarding_sounds_text
import whitenoise.composeapp.generated.resources.onboarding_sounds_title
import whitenoise.composeapp.generated.resources.onboarding_timer_text
import whitenoise.composeapp.generated.resources.onboarding_timer_title
import whitenoise.composeapp.generated.resources.onboarding_welcome_text
import whitenoise.composeapp.generated.resources.onboarding_welcome_title
import whitenoise.composeapp.generated.resources.skip

private class OnboardingPage(
    val title: StringResource,
    val text: StringResource,
    val art: @Composable (FontFamily) -> Unit
)

private val pages = listOf(
    OnboardingPage(Res.string.onboarding_welcome_title, Res.string.onboarding_welcome_text) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
    },
    OnboardingPage(Res.string.onboarding_sounds_title, Res.string.onboarding_sounds_text) {
        WaveformVisualizer(
            isPlaying = true,
            soundType = "ocean",
            modifier = Modifier.width(260.dp).height(120.dp)
        )
    },
    OnboardingPage(Res.string.onboarding_timer_title, Res.string.onboarding_timer_text) { font ->
        // The circle is drawn at a fixed 320dp; shrink it to fit the page.
        Box(modifier = Modifier.size(220.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.requiredSize(320.dp).scale(0.68f)) {
                GlowingTimerCircle(font = font, isActive = true, text = "30:00")
            }
        }
    },
    OnboardingPage(Res.string.onboarding_journal_title, Res.string.onboarding_journal_text) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Mint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_notes),
                contentDescription = null,
                tint = Mint,
                modifier = Modifier.size(80.dp)
            )
        }
    }
)

/** A few swipeable pages introducing the app, shown once on the first launch. */
@Composable
internal fun OnboardingScreen(onFinished: () -> Unit) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_starry),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        Column(
            // the backdrop goes edge to edge, the content stays clear of the system bars
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Skip (kept in place on the last page so nothing shifts) ---
            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (!isLastPage) {
                    TextButton(onClick = onFinished) {
                        Text(
                            text = stringResource(Res.string.skip),
                            fontFamily = quickSandFont,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                PageContent(pages[index], balooFont, quickSandFont)
            }

            PageDots(count = pages.size, current = pagerState.currentPage)

            DozzzeButton(
                modifier = Modifier.padding(top = 28.dp, bottom = 32.dp).height(56.dp),
                buttonText = stringResource(if (isLastPage) Res.string.get_started else Res.string.next),
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                buttonWidth = 226.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Mint,
                onClicked = {
                    if (isLastPage) onFinished()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            )
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage, titleFont: FontFamily, font: FontFamily) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.height(230.dp), contentAlignment = Alignment.Center) {
            page.art(font)
        }
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = titleFont,
            color = Mint,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = stringResource(page.text),
            fontFamily = font,
            fontSize = 17.sp,
            lineHeight = 25.sp,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun PageDots(count: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { index ->
            val selected = index == current
            val width by animateDpAsState(if (selected) 24.dp else 8.dp, label = "dotWidth")
            val color by animateColorAsState(
                if (selected) Mint else Color.White.copy(alpha = 0.3f),
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
