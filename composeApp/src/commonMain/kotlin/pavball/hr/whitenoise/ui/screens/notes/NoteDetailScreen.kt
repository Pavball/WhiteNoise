package pavball.hr.whitenoise.ui.screens.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.JournalEntry
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.ui.components.utils.formatDate
import pavball.hr.whitenoise.ui.theme.SlateBlue
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.cancel
import whitenoise.composeapp.generated.resources.close
import whitenoise.composeapp.generated.resources.delete
import whitenoise.composeapp.generated.resources.delete_entry_confirm
import whitenoise.composeapp.generated.resources.edit

// Configuration
private val LineColor = Color(0xFFE0E0E0)
private val CardBackground = Color.White
private val LineHeightSp = 28.sp

/** Shows a dream or sleep note in the notebook-style card, with edit and delete. */
@Composable
fun JournalDetailCard(
    entry: JournalEntry,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    fontFamily: FontFamily
) {
    var confirmingDelete by remember(entry.id) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(480.dp)
            .background(CardBackground, RoundedCornerShape(16.dp))
            // swallow taps so they don't reach the dismissing scrim behind the card
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            .padding(24.dp)
    ) {
        Column {
            // --- Header (date / stars + close) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (entry.type == JournalType.NOTE) {
                        Text(
                            text = formatDate(entry.createdAt),
                            fontFamily = fontFamily,
                            color = SlateBlue,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (entry.rating > 0 || entry.type == JournalType.DREAM) {
                        Stars(entry.rating)
                    }
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Notebook Area ---
            NotebookTextField(
                text = entry.text,
                fontFamily = fontFamily,
                modifier = Modifier.weight(1f)
            )

            // --- Actions ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (confirmingDelete) {
                    Text(
                        text = stringResource(Res.string.delete_entry_confirm),
                        fontFamily = fontFamily,
                        color = SlateBlue,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { confirmingDelete = false }) {
                        Text(stringResource(Res.string.cancel), fontFamily = fontFamily, color = SlateBlue)
                    }
                    TextButton(onClick = onDelete) {
                        Text(stringResource(Res.string.delete), fontFamily = fontFamily, color = Color(0xFFD32F2F))
                    }
                } else {
                    TextButton(onClick = { confirmingDelete = true }) {
                        Text(stringResource(Res.string.delete), fontFamily = fontFamily, color = Color(0xFFD32F2F))
                    }
                    TextButton(onClick = onEdit) {
                        Text(stringResource(Res.string.edit), fontFamily = fontFamily, color = SlateBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun Stars(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Custom Component that draws lines behind the text.
 */
@Composable
fun NotebookTextField(
    text: String,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // 1. The Lines (drawn on canvas)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineSpacing = LineHeightSp.toPx()
            // We draw lines starting from slightly below the top to align with text baseline
            val offset = lineSpacing * 0.8f

            // Calculate how many lines fit in the available height
            val numberOfLines = (size.height / lineSpacing).toInt()

            for (i in 0 until numberOfLines) {
                val y = (i * lineSpacing) + offset
                drawLine(
                    color = LineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // 2. The Text (placed on top, scrolls when it is longer than the card)
        Text(
            text = text,
            fontFamily = fontFamily,
            color = SlateBlue,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            lineHeight = LineHeightSp,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        )
    }
}
