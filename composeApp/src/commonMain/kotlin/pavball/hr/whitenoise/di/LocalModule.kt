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
import pavball.hr.whitenoise.domain.usecase.journal.DeleteJournalEntry
import pavball.hr.whitenoise.domain.usecase.journal.DeleteJournalEntryUseCase
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntries
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntriesUseCase
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntry
import pavball.hr.whitenoise.domain.usecase.journal.GetJournalEntryUseCase
import pavball.hr.whitenoise.domain.usecase.journal.SaveJournalEntry
import pavball.hr.whitenoise.domain.usecase.journal.SaveJournalEntryUseCase
import pavball.hr.whitenoise.domain.usecase.meditation.GetMeditationSessions
import pavball.hr.whitenoise.domain.usecase.meditation.GetMeditationSessionsUseCase
import pavball.hr.whitenoise.domain.usecase.meditation.SaveMeditationSession
import pavball.hr.whitenoise.domain.usecase.meditation.SaveMeditationSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.DeleteSleepSession
import pavball.hr.whitenoise.domain.usecase.sleep.DeleteSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.EndSleepSession
import pavball.hr.whitenoise.domain.usecase.sleep.EndSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.GetActiveSleepSession
import pavball.hr.whitenoise.domain.usecase.sleep.GetActiveSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.GetSleepSessions
import pavball.hr.whitenoise.domain.usecase.sleep.GetSleepSessionsUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.RateSleepSession
import pavball.hr.whitenoise.domain.usecase.sleep.RateSleepSessionUseCase
import pavball.hr.whitenoise.domain.usecase.sleep.StartSleepSession
import pavball.hr.whitenoise.domain.usecase.sleep.StartSleepSessionUseCase
import pavball.hr.whitenoise.repositories.CustomSoundRepository
import pavball.hr.whitenoise.repositories.CustomSoundRepositoryImpl
import pavball.hr.whitenoise.repositories.JournalRepository
import pavball.hr.whitenoise.repositories.JournalRepositoryImpl
import pavball.hr.whitenoise.repositories.MeditationRepository
import pavball.hr.whitenoise.repositories.MeditationRepositoryImpl
import pavball.hr.whitenoise.repositories.SleepRepository
import pavball.hr.whitenoise.repositories.SleepRepositoryImpl
import pavball.hr.whitenoise.source.local.Database
import pavball.hr.whitenoise.source.local.LocalDataSource
import pavball.hr.whitenoise.source.local.LocalDataSourceImpl
import pavball.hr.whitenoise.ui.viewmodels.JournalEditorViewModel
import pavball.hr.whitenoise.ui.viewmodels.JournalEditorViewModelImpl
import pavball.hr.whitenoise.ui.viewmodels.MeditationViewModel
import pavball.hr.whitenoise.ui.viewmodels.MeditationViewModelImpl
import pavball.hr.whitenoise.ui.viewmodels.NotesViewModel
import pavball.hr.whitenoise.ui.viewmodels.NotesViewModelImpl
import pavball.hr.whitenoise.ui.viewmodels.SleepViewModel
import pavball.hr.whitenoise.ui.viewmodels.SleepViewModelImpl
import pavball.hr.whitenoise.ui.viewmodels.TrackingViewModel
import pavball.hr.whitenoise.ui.viewmodels.TrackingViewModelImpl

val storageModule = module {


    single { Database(get()) }

    single<LocalDataSource> { LocalDataSourceImpl(get()) }

    single<CustomSoundRepository> { CustomSoundRepositoryImpl(get()) }
    single<JournalRepository> { JournalRepositoryImpl(get()) }
    single<SleepRepository> { SleepRepositoryImpl(get()) }
    single<MeditationRepository> { MeditationRepositoryImpl(get()) }

    // Use cases
    single<GetCustomSoundUseCase> { GetCustomSound(get()) }
    single<InsertCustomSoundUseCase> { InsertCustomSound(get()) }
    single<UpdateCustomSoundUseCase> { UpdateCustomSound(get()) }
    single<UpdateCustomSoundColorUseCase> { UpdateCustomSoundColor(get()) }
    single<DeleteCustomSoundUseCase> { DeleteCustomSound(get()) }

    single<GetJournalEntriesUseCase> { GetJournalEntries(get()) }
    single<GetJournalEntryUseCase> { GetJournalEntry(get()) }
    single<SaveJournalEntryUseCase> { SaveJournalEntry(get()) }
    single<DeleteJournalEntryUseCase> { DeleteJournalEntry(get()) }

    single<GetSleepSessionsUseCase> { GetSleepSessions(get()) }
    single<GetActiveSleepSessionUseCase> { GetActiveSleepSession(get()) }
    single<StartSleepSessionUseCase> { StartSleepSession(get()) }
    single<EndSleepSessionUseCase> { EndSleepSession(get()) }
    single<RateSleepSessionUseCase> { RateSleepSession(get()) }
    single<DeleteSleepSessionUseCase> { DeleteSleepSession(get()) }

    single<GetMeditationSessionsUseCase> { GetMeditationSessions(get()) }
    single<SaveMeditationSessionUseCase> { SaveMeditationSession(get()) }

    // Screen-scoped view models (the main player view model is platform specific)
    viewModel<NotesViewModel> { NotesViewModelImpl(get(), get()) }
    viewModel<JournalEditorViewModel> { JournalEditorViewModelImpl(get(), get()) }
    viewModel<SleepViewModel> { SleepViewModelImpl(get(), get(), get(), get(), get(), get(), get()) }
    viewModel<TrackingViewModel> { TrackingViewModelImpl(get(), get(), get()) }
    viewModel<MeditationViewModel> { MeditationViewModelImpl(get()) }
}
