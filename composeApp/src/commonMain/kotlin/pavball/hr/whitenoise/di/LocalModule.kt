package pavball.hr.whitenoise.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pavball.hr.whitenoise.domain.usecase.DeleteCustomSound
import pavball.hr.whitenoise.domain.usecase.DeleteCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.GetCustomSound
import pavball.hr.whitenoise.domain.usecase.GetCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.InsertCustomSound
import pavball.hr.whitenoise.domain.usecase.InsertCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSound
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundColor
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundColorUseCase
import pavball.hr.whitenoise.domain.usecase.UpdateCustomSoundUseCase
import pavball.hr.whitenoise.domain.usecase.user.Login
import pavball.hr.whitenoise.domain.usecase.user.LoginUseCase
import pavball.hr.whitenoise.domain.usecase.user.Logout
import pavball.hr.whitenoise.domain.usecase.user.LogoutUseCase
import pavball.hr.whitenoise.domain.usecase.user.ObserveUser
import pavball.hr.whitenoise.domain.usecase.user.ObserveUserUseCase
import pavball.hr.whitenoise.domain.usecase.user.Register
import pavball.hr.whitenoise.domain.usecase.user.RegisterUseCase
import pavball.hr.whitenoise.repositories.CustomSoundRepository
import pavball.hr.whitenoise.repositories.CustomSoundRepositoryImpl
import pavball.hr.whitenoise.repositories.UserRepository
import pavball.hr.whitenoise.repositories.UserRepositoryImpl
import pavball.hr.whitenoise.source.local.Database
import pavball.hr.whitenoise.source.local.LocalDataSource
import pavball.hr.whitenoise.source.local.LocalDataSourceImpl
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModelImpl

val storageModule = module {


    single { Database(get()) }

    single<LocalDataSource> { LocalDataSourceImpl(get()) }

    single<CustomSoundRepository> { CustomSoundRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    // Use cases
    single<GetCustomSoundUseCase> { GetCustomSound(get()) }
    single<InsertCustomSoundUseCase> { InsertCustomSound(get()) }
    single<UpdateCustomSoundUseCase> { UpdateCustomSound(get()) }
    single<UpdateCustomSoundColorUseCase> { UpdateCustomSoundColor(get()) }
    single<DeleteCustomSoundUseCase> { DeleteCustomSound(get()) }

    single<ObserveUserUseCase> { ObserveUser(get()) }
    single<LoginUseCase> { Login(get()) }
    single<RegisterUseCase> { Register(get()) }
    single<LogoutUseCase> { Logout(get()) }

    viewModel<ProfileViewModel>{ ProfileViewModelImpl(get(), get(), get(), get()) }
}