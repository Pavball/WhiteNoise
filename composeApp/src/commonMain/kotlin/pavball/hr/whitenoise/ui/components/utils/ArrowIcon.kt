package pavball.hr.whitenoise.ui.components.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_arrowright

@Composable
fun ArrowIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_arrowright),
        contentDescription = null, // purely decorative
        tint = Color.White,
        modifier = Modifier
            .size(24.dp)
            .padding(2.dp) // padding inside the circle
    )
}
