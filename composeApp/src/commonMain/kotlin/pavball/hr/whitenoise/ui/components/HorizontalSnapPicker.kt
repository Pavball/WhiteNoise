package pavball.hr.whitenoise.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HorizontalSnapPicker(
    items: List<T>,
    startIndex: Int = 0,
    itemWidth: Dp = 100.dp,
    visibleItemsCount: Int = 3, // How many items fit on screen? Used for padding.
    onSelect: (T) -> Unit
) {
    // 1. Force the list to scroll if the startIndex changes (e.g. DataStore loads)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    LaunchedEffect(startIndex) {
        if (!listState.isScrollInProgress) {
            listState.scrollToItem(startIndex)
        }
    }

    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

// 2. Track the "Selected Index" in a state variable so UI and Logic always match
    var currentSelectedIndex by remember { mutableStateOf(startIndex) }

// 3. The Math Block: Calculates strictly based on what is physically in the center
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val viewportCenter = layoutInfo.viewportSize.width / 2
                val visibleItems = layoutInfo.visibleItemsInfo

                if (visibleItems.isEmpty()) return@map -1

                // Find the item closest to the physical center of the screen
                val closestItem = visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + (item.size / 2)
                    abs(itemCenter - viewportCenter)
                }
                closestItem?.index ?: -1
            }
            .distinctUntilChanged()
            .collect { index ->
                if (index != -1) {
                    currentSelectedIndex = index
                    // Safe check to ensure we don't crash on empty lists
                    if (items.isNotEmpty()) {
                        onSelect(items[index.coerceIn(items.indices)])
                    }
                }
            }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // 4. Precise Padding Calculation
        // We calculate padding dynamically so the first and last items can reach the exact center.
        val halfRowWidth = maxWidth / 2
        val halfItemWidth = itemWidth / 2
        val contentPadding = PaddingValues(horizontal = halfRowWidth - halfItemWidth)

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size) { index ->
                val item = items[index]

                // 5. Visual Highlight Logic
                // We rely ONLY on currentSelectedIndex. No separate math here.
                val isSelected = (index == currentSelectedIndex)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(itemWidth) // CRITICAL: This must match the itemWidth param
                        .height(100.dp)
                        .graphicsLayer {
                            val scale = if (isSelected) 1.2f else 1.0f
                            scaleX = scale
                            scaleY = scale
                            alpha = if (isSelected) 1f else 0.5f
                        }
                        .background(
                            color = if (isSelected) Color.Blue else Color.LightGray,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = item.toString(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}