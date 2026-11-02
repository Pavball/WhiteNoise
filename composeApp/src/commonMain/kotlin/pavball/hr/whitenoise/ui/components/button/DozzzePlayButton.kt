package pavball.hr.whitenoise.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.Mint
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.pause
import whitenoise.composeapp.generated.resources.play

/** The round mint play / pause button of the player and the mixer. */
@Composable
fun DozzzePlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 12.dp, shape = CircleShape, ambientColor = Mint, spotColor = Mint)
            .clip(CircleShape)
            .background(Mint)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(if (isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
            contentDescription = stringResource(if (isPlaying) Res.string.pause else Res.string.play),
            tint = DarkNavy,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}
