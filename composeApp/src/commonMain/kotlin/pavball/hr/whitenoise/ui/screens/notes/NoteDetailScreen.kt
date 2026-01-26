package pavball.hr.whitenoise.ui.screens.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pavball.hr.whitenoise.domain.model.NoteItem
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.SlateBlue

// Configuration
private val LineColor = Color(0xFFE0E0E0)
private val CardBackground = Color.White
private val LineHeightSp = 28.sp

@Composable
fun DreamDetailCard(
    note: NoteItem,
    onClose: () -> Unit,
    fontFamily: androidx.compose.ui.text.font.FontFamily
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(450.dp)
            .background(CardBackground, RoundedCornerShape(16.dp))
            .clickable(enabled = false) {}
            .padding(24.dp)
    ) {
        Column {
            // --- Header (Stars Only + Close) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars (No Date)
                Row {
                    val starCount = note.detail.firstOrNull()?.digitToIntOrNull() ?: 0
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < starCount) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Close Button
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Notebook Area ---
            NotebookTextField(
                text = note.fullText,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
fun SleepDetailCard(
    note: NoteItem,
    onClose: () -> Unit,
    fontFamily: androidx.compose.ui.text.font.FontFamily
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(450.dp)
            .background(CardBackground, RoundedCornerShape(16.dp))
            .clickable(enabled = false) {}
            .padding(24.dp)
    ) {
        Column {
            // --- Header (Date & Hours in one line) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${note.date} - ${note.detail} of Sleep",
                    fontFamily = fontFamily,
                    color = SlateBlue,
                    style = MaterialTheme.typography.bodySmall ,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Notebook Area ---
            NotebookTextField(
                text = note.fullText,
                fontFamily = fontFamily
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
    fontFamily: androidx.compose.ui.text.font.FontFamily
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

        // 2. The Text (placed on top)
        Text(
            text = text,
            fontFamily = fontFamily,
            color = SlateBlue,
            style = MaterialTheme.typography.bodySmall ,
            fontWeight = FontWeight.Medium,
            lineHeight = LineHeightSp,
            modifier = Modifier.fillMaxSize()
        )
    }
}