package pavball.hr.whitenoise.ui.screens.dream

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.input.DreamJournalInput
import pavball.hr.whitenoise.ui.components.input.RatingBar
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.JournalEditorViewModel
import pavball.hr.whitenoise.ui.viewmodels.JournalEditorViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_starry
import whitenoise.composeapp.generated.resources.dream_placeholder
import whitenoise.composeapp.generated.resources.my_dream
import whitenoise.composeapp.generated.resources.note_placeholder
import whitenoise.composeapp.generated.resources.save
import whitenoise.composeapp.generated.resources.sleep_note

/** Writes a new dream or sleep note, or edits an existing one ([entryId]). */
@Composable
internal fun DreamScreen(
    modifier: Modifier = Modifier,
    type: JournalType,
    entryId: Long?,
    onSaved: () -> Unit,
    viewModel: JournalEditorViewModel = koinViewModel()
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<JournalEditorViewState>()
        .collectAsState(initial = JournalEditorViewState(type = type))

    LaunchedEffect(type, entryId) { viewModel.start(type, entryId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onSaved() }

    val isDream = state.type == JournalType.DREAM

    // Typed text lives here so the field never waits on the view state; it is
    // seeded once the entry has loaded and mirrored to the view model.
    var text by rememberSaveable(state.isLoaded) { mutableStateOf(state.text) }

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
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(48.dp)

            Text(
                text = stringResource(if (isDream) Res.string.my_dream else Res.string.sleep_note),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal
            )

            SpacerHelper(48.dp)

            DreamJournalInput(
                value = text,
                onValueChange = {
                    text = it
                    viewModel.updateText(it)
                },
                placeholder = stringResource(if (isDream) Res.string.dream_placeholder else Res.string.note_placeholder),
                customFont = quickSandFont
            )

            SpacerHelper(11.dp)

            RatingBar(
                currentRating = state.rating,
                onRatingChanged = { newRating ->
                    // tapping the current rating again clears it
                    viewModel.updateRating(if (newRating == state.rating) 0 else newRating)
                }
            )
            SpacerHelper(30.dp)

            DozzzeButton(
                onClicked = { viewModel.save() },
                buttonText = stringResource(Res.string.save),
                buttonWidth = 120.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = if (text.isNotBlank()) LightGray else LightGray.copy(alpha = 0.4f)
            )

            SpacerHelper(48.dp)
        }
    }

}
