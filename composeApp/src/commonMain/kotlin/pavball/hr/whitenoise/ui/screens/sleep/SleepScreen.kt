package pavball.hr.whitenoise.ui.screens.sleep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import whitenoise.composeapp.generated.resources.bg_sea
import whitenoise.composeapp.generated.resources.sleep

@Composable
fun SleepScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_sea),
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
                text = stringResource(Res.string.sleep),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal
            )

            SpacerHelper(92.dp)

            DreamJournalInput(quickSandFont)

            SpacerHelper(50.dp)

            Text(
                "When do you wanna wake up?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryFixed,
                fontFamily = quickSandFont,
                fontWeight = FontWeight.Medium
            )
            SpacerHelper(20.dp)

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = modifier.padding(bottom = 4.dp),
                    text = "4    59",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryFixed.copy(alpha = 0.4f),
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    modifier = modifier.padding(bottom = 4.dp),
                    text = "5    00",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryFixed,
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    modifier = modifier.padding(end = 4.dp),
                    text = "5    01",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryFixed.copy(alpha = 0.4f),
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.SemiBold
                )
            }

            SpacerHelper(10.dp)

            DozzzeButton(
                onClicked = {},
                buttonText = "Set alarm & start sleep timer",
                buttonWidth = 200.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }

}