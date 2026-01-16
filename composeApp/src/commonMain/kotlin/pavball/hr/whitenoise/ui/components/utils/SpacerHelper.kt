package pavball.hr.whitenoise.ui.components.utils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpacerHelper(dp: Dp){
    Spacer(modifier = Modifier.height(dp))
}