package pavball.hr.whitenoise.ui.screens.dream

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.input.DreamJournalInput
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_starry
import whitenoise.composeapp.generated.resources.save

@Composable
internal fun DreamScreen(
    modifier: Modifier = Modifier
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

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
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "My Dream",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal
            )

            SpacerHelper(92.dp)

            DreamJournalInput(quickSandFont)

            SpacerHelper(30.dp)

            DozzzeButton(
                onClicked = {},
                buttonText = stringResource(Res.string.save),
                buttonWidth = 100.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

}