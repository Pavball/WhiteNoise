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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.domain.model.NoteItem
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.profile.ArrowIcon
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_moonlight
import whitenoise.composeapp.generated.resources.ic_arrowright

@Composable
fun NotesScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // Mock Data based on your image
    val notes = listOf(
        NoteItem("Dream", "14.01.2026.", "5★", "Sanjala sam da sam prošla MMK2..."),
        NoteItem("Note", "12.01.2026.", "7h 5m", "Trajanje: 7h 05m (Solidno)\nKvaliteta: ★★★★☆ (4/5)\nBuđenje: 7:00 AM\nTijelo: Odmorno\nMentalno: Bistra glava"),
        NoteItem("Dream", "11.01.2026.", "4★", "Walking through a dense forest..."),
        NoteItem("Dream", "10.01.2026.", "3★", "Flying over the city..."),
        NoteItem("Note", "09.01.2026.", "8h 0m", "Trajanje: 8h 00m\nKvaliteta: ★★★★★"),
        NoteItem("Note", "07.01.2026.", "5h 45m", "Short sleep, feeling tired.")
    )

    // State for showing details
    var selectedNote by remember { mutableStateOf<NoteItem?>(null) }

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
            SpacerHelper(60.dp)

            Text(
                text = "Notes",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(30.dp)

            // List of Notes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notes) { note ->
                    NoteItemRow(
                        note = note,
                        fontFamily = quickSandFont,
                        onClick = { selectedNote = note }
                    )
                }
                item { SpacerHelper(100.dp) } // Bottom padding
            }
        }

        // 4. Modal Overlay (Detail View)
        if (selectedNote != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedNote = null }, // Dismiss on background click
                contentAlignment = Alignment.Center
            ) {
                if (selectedNote!!.type == "Dream") {
                    DreamDetailCard(
                        note = selectedNote!!,
                        onClose = { selectedNote = null },
                        fontFamily = quickSandFont
                    )
                } else {
                    SleepDetailCard(
                        note = selectedNote!!,
                        onClose = { selectedNote = null },
                        fontFamily = quickSandFont
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItemRow(
    note: NoteItem,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
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
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Type (Dream/Note)
        Text(
            text = note.type,
            color = LightGray,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.width(70.dp)
        )

        // Middle: Date
        Text(
            text = note.date,
            color = LightGray,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.displaySmall
        )

        // Right Section: Detail + Arrow
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = note.detail,
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