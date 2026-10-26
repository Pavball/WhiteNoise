package pavball.hr.whitenoise.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pavball.hr.whitenoise.platform.AndroidBackgroundPlayback
import pavball.hr.whitenoise.platform.AndroidWakeUpAlarm
import pavball.hr.whitenoise.platform.BackgroundPlayback
import pavball.hr.whitenoise.platform.WakeUpAlarm
import pavball.hr.whitenoise.source.local.DriverFactory
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl

val homeScreenAndroidModule = module {
    viewModelOf(::MainScreenViewModelImpl) bind MainScreenViewModel::class

    single { DriverFactory(get()) }
    single { SettingsDataStore(get()) }
    single<WakeUpAlarm> { AndroidWakeUpAlarm(get()) }
    single<BackgroundPlayback> { AndroidBackgroundPlayback(get()) }


}
