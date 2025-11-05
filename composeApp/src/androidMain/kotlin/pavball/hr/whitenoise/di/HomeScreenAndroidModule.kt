package pavball.hr.whitenoise.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl

val homeScreenAndroidModule = module {
    factoryOf(::MainScreenViewModelImpl) bind MainScreenViewModel::class
}

