package pavball.hr.whitenoise.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pavball.hr.whitenoise.domain.usecase.DeleteCustomSound
import pavball.hr.whitenoise.domain.usecase.DeleteCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.GetCustomSound
import pavball.hr.whitenoise.domain.usecase.GetCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.InsertCustomSound
import pavball.hr.whitenoise.domain.usecase.InsertCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSound
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundUseCase
import pavball.hr.whitenoise.source.local.DriverFactory
import pavball.hr.whitenoise.ui.components.SettingsDataStore
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl

val homeScreenAndroidModule = module {
    factoryOf(::MainScreenViewModelImpl) bind MainScreenViewModel::class

    single { DriverFactory(get()) }
    single { SettingsDataStore(get()) }


}

