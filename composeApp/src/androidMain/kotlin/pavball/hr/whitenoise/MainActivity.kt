package pavball.hr.whitenoise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import pavball.hr.whitenoise.di.sharedAndroidKoinModules
import pavball.hr.whitenoise.di.sharedKoinModules
import pavball.hr.whitenoise.ui.screens.main.App
import pavball.hr.whitenoise.ui.theme.AppTheme
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val modules = sharedKoinModules + sharedAndroidKoinModules

        startKoin {
            androidContext(this@MainActivity)
            modules(modules)
        }

        setContent {
            val viewModel = koinViewModel<MainScreenViewModel>()
            val state by viewModel.viewState<MainScreenViewState>()
                .collectAsState(initial = MainScreenViewState())

            AppTheme(themeMode = state.themeMode) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}