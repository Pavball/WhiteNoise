package pavball.hr.whitenoise.ui.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.ArrowIcon
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.formatDate
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.NotesViewModel
import pavball.hr.whitenoise.ui.viewmodels.NotesViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_moonlight
import whitenoise.composeapp.generated.resources.dream
import whitenoise.composeapp.generated.resources.new_dream
import whitenoise.composeapp.generated.resources.new_note
import whitenoise.composeapp.generated.resources.no_notes
import whitenoise.composeapp.generated.resources.note
import whitenoise.composeapp.generated.resources.notes

@Composable
internal fun NotesScreen(
    modifier: Modifier = Modifier,
    onNewEntry: (JournalType) -> Unit,
    onEditEntry: (JournalEntry) -> Unit,
    viewModel: NotesViewModel = koinViewModel()
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<NotesViewState>()
        .collectAsState(initial = NotesViewState())

    // Id of the entry shown in the detail card
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selectedEntry = state.entries.firstOrNull { it.id == selectedId }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background
        Image(
            painter = painterResource(Res.drawable.bg_moonlight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        // 3. Main Content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpacerHelper(48.dp)

            Text(
                text = stringResource(Res.string.notes),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(20.dp)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DozzzeButton(
                    onClicked = { onNewEntry(JournalType.DREAM) },
                    buttonText = stringResource(Res.string.new_dream),
                    buttonWidth = 130.dp,
                    buttonTextStyle = MaterialTheme.typography.bodySmall,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )
                DozzzeButton(
                    onClicked = { onNewEntry(JournalType.NOTE) },
                    buttonText = stringResource(Res.string.new_note),
                    buttonWidth = 130.dp,
                    buttonTextStyle = MaterialTheme.typography.bodySmall,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            SpacerHelper(24.dp)

            if (state.isLoaded && state.entries.isEmpty()) {
                Text(
                    text = stringResource(Res.string.no_notes),
                    fontFamily = quickSandFont,
                    color = LightGray.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            // List of Notes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.entries, key = { it.id }) { entry ->
                    NoteItemRow(
                        entry = entry,
                        fontFamily = quickSandFont,
                        onClick = { selectedId = entry.id }
                    )
                }
                item { SpacerHelper(24.dp) } // Bottom padding
            }
        }

        // 4. Modal Overlay (Detail View)
        if (selectedEntry != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedId = null }, // Dismiss on background click
                contentAlignment = Alignment.Center
            ) {
                JournalDetailCard(
                    entry = selectedEntry,
                    onClose = { selectedId = null },
                    onEdit = {
                        selectedId = null
                        onEditEntry(selectedEntry)
                    },
                    onDelete = {
                        selectedId = null
                        viewModel.deleteEntry(selectedEntry.id)
                    },
                    fontFamily = quickSandFont
                )
            }
        }
    }
}

@Composable
fun NoteItemRow(
    entry: JournalEntry,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                border = BorderStroke(1.dp, Mint), // Mint border
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Type (Dream/Note)
        Text(
            text = stringResource(if (entry.type == JournalType.DREAM) Res.string.dream else Res.string.note),
            color = LightGray,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.width(70.dp)
        )

        // Middle: Date
        Text(
            text = formatDate(entry.createdAt),
            color = LightGray,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.displaySmall
        )

        // Right Section: Rating + Arrow
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (entry.rating > 0) "${entry.rating}★" else "",
                color = LightGray,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Arrow in Circle
            ArrowIcon()
        }
    }
}
