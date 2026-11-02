package pavball.hr.whitenoise.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * An [AlertDialog] in the app's colors that stays readable in light and dark mode.
 *
 * DozzzeTypography gives some text styles a fixed color (dark navy) meant for
 * light backgrounds, and a style's color wins over the dialog's content color,
 * so those colors are cleared here and the text follows the theme instead.
 */
@Composable
fun DozzzeAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography.let {
        it.copy(
            headlineSmall = it.headlineSmall.copy(color = Color.Unspecified),
            bodyMedium = it.bodyMedium.copy(color = Color.Unspecified),
            bodySmall = it.bodySmall.copy(color = Color.Unspecified)
        )
    }
    MaterialTheme(typography = typography) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            modifier = modifier,
            dismissButton = dismissButton,
            title = title,
            text = text,
            containerColor = colors.surface,
            titleContentColor = colors.onSurface,
            textContentColor = colors.onSurface.copy(alpha = 0.85f)
        )
    }
}
