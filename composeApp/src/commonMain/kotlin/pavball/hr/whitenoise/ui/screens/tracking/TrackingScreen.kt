package pavball.hr.whitenoise.ui.screens.tracking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.formatClockTime
import pavball.hr.whitenoise.ui.components.utils.formatDuration
import pavball.hr.whitenoise.ui.components.utils.shortLabel
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.TrackedDay
import pavball.hr.whitenoise.ui.viewmodels.TrackingViewModel
import pavball.hr.whitenoise.ui.viewmodels.TrackingViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.average
import whitenoise.composeapp.generated.resources.bg_canyon
import whitenoise.composeapp.generated.resources.meditation_this_week
import whitenoise.composeapp.generated.resources.no_nights
import whitenoise.composeapp.generated.resources.sleep_notes
import whitenoise.composeapp.generated.resources.sleep_quality
import whitenoise.composeapp.generated.resources.time_in_bed
import whitenoise.composeapp.generated.resources.total_nights
import whitenoise.composeapp.generated.resources.tracking
import whitenoise.composeapp.generated.resources.woke_up
import whitenoise.composeapp.generated.resources.write_note
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private const val NO_VALUE = "–"

@Composable
internal fun TrackingScreen(
    modifier: Modifier = Modifier,
    onWriteNote: () -> Unit,
    viewModel: TrackingViewModel = koinViewModel()
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<TrackingViewState>()
        .collectAsState(initial = TrackingViewState())

// Colors extracted from the image style
    val mintColor = Color(0xFF98CBB4) // Light mint green for title and graph
    val graphLineColor = Color(0xFFECECEC) // Light grey/white for horizontal lines

    Box(modifier = Modifier.fillMaxSize()) {
        // --- Existing Background Setup ---
        Image(
            painter = painterResource(Res.drawable.bg_canyon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)) // Slightly darker for contrast
        )

        // --- Main Content ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(48.dp) // Top padding

            // 1. Title
            Text(
                text = stringResource(Res.string.tracking),
                style = MaterialTheme.typography.displaySmall,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = mintColor,
                fontSize = 32.sp
            )

            SpacerHelper(32.dp)

            // 2. The Custom Graph
            if (state.week.isNotEmpty()) {
                SleepGraph(
                    week = state.week,
                    font = quickSandFont,
                    lineColor = mintColor,
                    gridColor = graphLineColor
                )
            }

            if (state.isLoaded && state.totalNights == 0) {
                Text(
                    text = stringResource(Res.string.no_nights),
                    fontFamily = quickSandFont,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            SpacerHelper(28.dp)

            // 3. Stats List
            val last = state.lastNight
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatRow(
                    stringResource(Res.string.time_in_bed),
                    last?.endedAt?.let { "${formatClockTime(last.startedAt)} - ${formatClockTime(it)}" } ?: NO_VALUE,
                    quickSandFont
                )
                StatRow(
                    stringResource(Res.string.sleep_quality),
                    state.averageQuality?.let { "${(it * 20).roundToInt()}%" } ?: NO_VALUE,
                    quickSandFont
                )
                StatRow(
                    stringResource(Res.string.woke_up),
                    last?.endedAt?.let(::formatClockTime) ?: NO_VALUE,
                    quickSandFont
                )
                StatRow(stringResource(Res.string.sleep_notes), state.latestNote ?: NO_VALUE, quickSandFont)
                StatRow(stringResource(Res.string.total_nights), state.totalNights.toString(), quickSandFont)
                StatRow(
                    stringResource(Res.string.average),
                    state.averageSleepMs?.let(::formatDuration) ?: NO_VALUE,
                    quickSandFont
                )
                StatRow(
                    stringResource(Res.string.meditation_this_week),
                    "${state.meditationMinutesThisWeek} min",
                    quickSandFont
                )
            }

            SpacerHelper(32.dp)

            // 4. Button
            DozzzeButton(
                onClicked = onWriteNote,
                buttonText = stringResource(Res.string.write_note),
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
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(max = 180.dp).padding(start = 12.dp)
        )
    }
}

/** Hours slept on each of the last 7 days, scaled to the range of the data. */
@Composable
private fun SleepGraph(
    week: List<TrackedDay>,
    font: FontFamily,
    lineColor: Color,
    gridColor: Color
) {
    // Height configuration
    val graphHeight = 150.dp
    val textLineHeight = 20.dp
    val yLabelsHeight = graphHeight + textLineHeight

    // Y axis: whole half-hours around the data, at least 2h tall (defaults to 7h–9h).
    val hours = week.mapNotNull { day -> day.sleepMs?.let { it / 3_600_000f } }
    var minHours = hours.minOrNull()?.let { floor(it * 2) / 2 } ?: 7f
    var maxHours = hours.maxOrNull()?.let { ceil(it * 2) / 2 } ?: 9f
    if (maxHours - minHours < 2f) {
        val pad = (2f - (maxHours - minHours)) / 2
        minHours = (minHours - pad).coerceAtLeast(0f)
        maxHours = minHours + 2f
    }
    val steps = 4
    val yLabels = (steps downTo 0).map { i -> minHours + (maxHours - minHours) * i / steps }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(yLabelsHeight + 30.dp)
    ) {
        // --- Y-Axis Labels ---
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .height(yLabelsHeight)
                .offset(y = -(textLineHeight / 2))
                .padding(end = 8.dp)
        ) {
            yLabels.forEach { value ->
                Text(
                    text = "${formatHours(value)}h",
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

                    // 2. Calculate Coordinates (days without a night are skipped)
                    val columnWidth = width / week.size
                    val points = week.mapIndexedNotNull { index, day ->
                        val slept = day.sleepMs ?: return@mapIndexedNotNull null
                        val normalized = ((slept / 3_600_000f - minHours) / (maxHours - minHours)).coerceIn(0f, 1f)
                        Offset((columnWidth * index) + (columnWidth / 2f), height - (normalized * height))
                    }

                    // 3. Draw the Line (Middle Layer)
                    if (points.size > 1) {
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
                    }

                    // 4. Draw Dots (Top Layer) - they cover the line ends
                    points.forEach { point ->
                        drawCircle(
                            color = gridColor,
                            radius = 20f,
                            center = point
                        )
                    }
                }
            }

            // X-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    Text(
                        text = day.dayOfWeek.shortLabel(),
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

/** 7.0 -> "7", 7.5 -> "7.5" */
private fun formatHours(value: Float): String {
    val rounded = (value * 10).roundToInt() / 10f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}
