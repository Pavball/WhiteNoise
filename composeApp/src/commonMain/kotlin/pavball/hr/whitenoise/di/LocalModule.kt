package pavball.hr.whitenoise.di

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
import pavball.hr.whitenoise.repositories.CustomSoundRepository
import pavball.hr.whitenoise.repositories.CustomSoundRepositoryImpl
import pavball.hr.whitenoise.source.local.Database
import pavball.hr.whitenoise.source.local.LocalDataSource
import pavball.hr.whitenoise.source.local.LocalDataSourceImpl

val storageModule = module {


    single { Database(get()) }

    single<LocalDataSource> { LocalDataSourceImpl(get()) }

    single<CustomSoundRepository> { CustomSoundRepositoryImpl(get()) }

    // Use cases
    single<GetCustomSoundUseCase> { GetCustomSound(get()) }
    single<InsertCustomSoundUseCase> { InsertCustomSound(get()) }
    single<UpdateCustomSoundUseCase> { UpdateCustomSound(get()) }
    single<UpdateCustomSoundColorUseCase> { UpdateCustomSoundColor(get()) }
    single<DeleteCustomSoundUseCase> { DeleteCustomSound(get()) }
}