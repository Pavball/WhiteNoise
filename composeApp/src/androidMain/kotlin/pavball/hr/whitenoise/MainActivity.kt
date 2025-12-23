package pavball.hr.whitenoise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import pavball.hr.whitenoise.di.sharedAndroidKoinModules
import pavball.hr.whitenoise.di.sharedKoinModules
import pavball.hr.whitenoise.ui.screens.main.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Safety check: Ensure Koin isn't already started (prevents crashes on rotation)
        if (GlobalContext.getOrNull() == null) {
            val modules = sharedKoinModules + sharedAndroidKoinModules
            startKoin {
                androidContext(this@MainActivity)
                modules(modules)
            }
        }

        setContent {
            // No Theme here. Just call the shared App entry point.
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

