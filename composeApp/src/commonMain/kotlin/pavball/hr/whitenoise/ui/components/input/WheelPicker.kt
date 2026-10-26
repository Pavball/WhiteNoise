package pavball.hr.whitenoise.ui.components.input

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * A vertical wheel showing three values with the selected one in the middle,
 * like the hour/minute wheels of an alarm clock.
 */
@Composable
fun WheelPicker(
    values: List<Int>,
    selected: Int,
    onSelected: (Int) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 40.dp,
    width: Dp = 64.dp,
    format: (Int) -> String = { it.toString().padStart(2, '0') }
) {
    val selectedIndex = values.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Start)
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    // The item whose top is nearest the top of the (padded) window is the centered one.
    val centeredIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            listState.firstVisibleItemIndex + if (offset > itemHeightPx / 2) 1 else 0
        }
    }

    // Report the value once scrolling settles.
    val currentValues by rememberUpdatedState(values)
    val currentOnSelected by rememberUpdatedState(onSelected)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect {
                val value = currentValues[centeredIndex.coerceIn(currentValues.indices)]
                currentOnSelected(value)
            }
    }

    // Follow selections made elsewhere (e.g. a restored wake-up time).
    LaunchedEffect(selectedIndex) {
        if (!listState.isScrollInProgress && centeredIndex != selectedIndex) {
            listState.scrollToItem(selectedIndex)
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = itemHeight),
        modifier = modifier.width(width).height(itemHeight * 3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(values.size) { index ->
            Box(
                modifier = Modifier.fillMaxWidth().height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = format(values[index]),
                    style = textStyle,
                    color = if (index == centeredIndex) Color.White else Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}
