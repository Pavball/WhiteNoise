package pavball.hr.whitenoise.ui.screens.tracking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_sea

@Composable
fun TrackingScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

// Colors extracted from the image style
    val mintColor = Color(0xFF98CBB4) // Light mint green for title and graph
    val graphLineColor = Color(0xFFECECEC) // Light grey/white for horizontal lines

    Box(modifier = Modifier.fillMaxSize()) {
        // --- Existing Background Setup ---
        Image(
            painter = painterResource(Res.drawable.bg_sea),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // Slightly darker for contrast
        )

        // --- Main Content ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(60.dp) // Top padding

            // 1. Title
            Text(
                text = "Tracking",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = mintColor,
                fontSize = 32.sp
            )

            SpacerHelper(40.dp)

            // 2. The Custom Graph
            SleepGraph(
                font = quickSandFont,
                lineColor = mintColor,
                gridColor = graphLineColor
            )

            SpacerHelper(35.dp)

            // 3. Stats List
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatRow("Time in Bed", "22:46 - 6:32", quickSandFont)
                StatRow("Sleep Quality", "69%", quickSandFont)
                StatRow("Snore", "0 min", quickSandFont)
                StatRow("Woke Up", "6:27", quickSandFont)
                StatRow("Sleep Notes", "Zagreb, Faks", quickSandFont)
                StatRow("Total Nights", "20", quickSandFont)
                StatRow("Average", "7h & 45m", quickSandFont)
            }

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            // 4. Button
            DozzzeButton(
                onClicked = {},
                buttonText = "Write a note",
                buttonWidth = 170.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(24.dp)
            )

            SpacerHelper(40.dp) // Bottom padding
        }
    }
}

// --- Helper Components ---

@Composable
fun StatRow(label: String, value: String, font: FontFamily) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontFamily = font,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun SleepGraph(
    font: FontFamily,
    lineColor: Color,
    gridColor: Color
) {
    // Height configuration
    val graphHeight = 150.dp
    val textLineHeight = 20.dp
    val yLabelsHeight = graphHeight + textLineHeight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(yLabelsHeight + 30.dp)
    ) {
        // --- Y-Axis Labels (Unchanged) ---
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .height(yLabelsHeight)
                .offset(y = -(textLineHeight / 2))
                .padding(end = 8.dp)
        ) {
            listOf("9h", "8.5h", "8h", "7.5h", "7h").forEach { text ->
                Text(
                    text = text,
                    color = Color.White,
                    fontFamily = font,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        // --- Chart Area ---
        Column(modifier = Modifier.weight(1f)) {

            Box(
                modifier = Modifier
                    .height(graphHeight)
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // 1. Draw Grid Lines (Bottom Layer)
                    val steps = 4
                    val stepHeight = height / steps

                    for (i in 0..steps) {
                        val y = i * stepHeight
                        val isDashed = i == 2

                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 3f,
                            pathEffect = if (isDashed) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                        )
                    }

                    // 2. Calculate Coordinates
                    val dataPoints = listOf(0.0f, 0.5f, 0.25f, 0.25f, 0.75f, 0.75f, 1.0f)
                    val columnWidth = width / dataPoints.size

                    // Store coordinates so we can use them twice
                    val points = dataPoints.mapIndexed { index, normalizedValue ->
                        val x = (columnWidth * index) + (columnWidth / 2f)
                        val y = height - (normalizedValue * height)
                        Offset(x, y)
                    }

                    // 3. Draw the Line (Middle Layer)
                    val path = Path().apply {
                        points.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    // 4. Draw Dots (Top Layer) - Now they will cover the line ends
                    points.forEachIndexed { index, point ->
                        drawCircle(
                            color =  gridColor,
                            radius = 20f,
                            center = point
                        )
                    }
                }
            }

            // X-Axis Labels (Unchanged)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { day ->
                    Text(
                        text = day,
                        color = Color.White,
                        fontFamily = font,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}