package pavball.hr.whitenoise.di

import android.content.Context
import org.koin.dsl.module
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl

val mainScreenAndroidModule = module {

    single { Context }

    single<MainScreenViewModel> { MainScreenViewModelImpl(
        context = get()
    ) }

}