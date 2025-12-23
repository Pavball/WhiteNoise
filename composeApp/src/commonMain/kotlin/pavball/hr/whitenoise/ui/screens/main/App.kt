package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import pavball.hr.whitenoise.ui.theme.DozzzeTheme
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState.Companion.ThemeMode

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.viewState.collectAsState(initial = MainScreenViewState())

    val isDark = when(state.themeMode){
        ThemeMode.DARK.name -> true
        ThemeMode.LIGHT.name -> false
        ThemeMode.SYSTEM.name -> isSystemInDarkTheme()
        else -> {
            isSystemInDarkTheme()
        }
    }

    DozzzeTheme(isDarkMode = isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}