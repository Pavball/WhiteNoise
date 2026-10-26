# CLAUDE.md

Guidance for working in this repository.

## Project

**WhiteNoise** (in-app brand: **Dozzze**) — a Compose Multiplatform sleep/relaxation app targeting Android and iOS.
Package / applicationId: `pavball.hr.whitenoise`. Single Gradle module: `:composeApp`.

Features: white noise player (bundled + user-added custom sounds), sleep timer with fade-out, waveform visualizer,
background playback with a media notification (lock screen / headset controls), theme mode (light/dark/system),
dream journal + sleep notes (Notes), bedtime with a Clock-app wake-up alarm (Sleep), sleep stats (Tracking),
guided breathing (Meditations) and Settings. All data is local (SQLDelight).

Android is the primary, working target. iOS is a shell (see "Platform status").

## Commands

Run from the repo root (Windows: use `gradlew.bat` or `./gradlew` from Git Bash).

```bash
./gradlew :composeApp:assembleDebug              # build Android debug APK
./gradlew :composeApp:installDebug               # install on connected device/emulator
./gradlew :composeApp:generateCommonMainAppDatabaseInterface   # regenerate SQLDelight code
./gradlew :composeApp:compileKotlinIosSimulatorArm64           # compile iOS (needs macOS)
```

iOS app: open `iosApp/iosApp.xcodeproj` in Xcode (macOS only). There are no tests in the project.

## Tech stack

- Kotlin 2.2.21, Compose Multiplatform 1.9.2, Material 3, AGP 8.9.3, JVM 11
- Navigation: `androidx.navigation:navigation-compose` (string routes)
- DI: Koin 4.1 (`koin-compose-viewmodel`)
- DB: SQLDelight 2.2 — database `AppDatabase`, package `pavball.hr.whitenoise.db`
- Settings: AndroidX DataStore Preferences (Android only)
- Audio: Media3 ExoPlayer + MediaSessionService (Android only)
- Also: skydoves colorpicker-compose, kotlinx-datetime

All versions live in `gradle/libs.versions.toml`. Note: `compileSdk`/`targetSdk` are hardcoded to 36 in
`composeApp/build.gradle.kts`, not taken from the catalog.

## Source layout

```
composeApp/src/
  commonMain/kotlin/pavball/hr/whitenoise/
    di/                 storageModule (LocalModule.kt), sharedKoinModules (SharedModules.kt)
    domain/model/       CustomSound, JournalEntry, SleepSession, MeditationSession, UserSound (expect rememberSoundPicker)
    domain/usecase/     one interface + impl per operation
    repositories/       CustomSound/Journal/Sleep/Meditation repositories (interface + internal Impl)
    source/local/       Database, DriverFactory (expect), LocalDataSource
    platform/           interfaces for platform services (WakeUpAlarm, BackgroundPlayback), bound per platform
    ui/components/      reusable composables (appbar, button, dialog, effects, input, utils)
    ui/screens/         one package per feature; main/ holds App, MainScreen (NavHost), Screens
    ui/theme/           DozzzeTheme, Color, Type
    ui/viewmodels/      BaseViewModel, *Contract.kt (abstract VM + ViewState), shared VM impls
  commonMain/sqldelight/pavball/hr/whitenoise/db/   CustomSound, Journal, Sleep, Meditation .sq (+ ../migrations/*.sqm)
  commonMain/composeResources/                      drawable, font, files, values
  androidMain/          DozzzeApplication (starts Koin), MainActivity, Android DI, MainScreenViewModelImpl,
                        playback/ (PlaybackService, DozzzePlayer, PlaybackContract), SettingsDataStore,
                        DriverFactory/UserSound actuals, res/raw/*.mp3
  iosMain/              MainViewController
  nativeMain/           DriverFactory/UserSound actuals
```

## Architecture & conventions

Layering: **Screen → ViewModel → UseCase → Repository → LocalDataSource → SQLDelight**.

- **ViewModels** extend `BaseViewModel<State>` (`ui/viewmodels/BaseViewModel.kt`). State is exposed as
  `viewState: SharedFlow<State>`. Use `runCommand { }` to launch work, `updateState { copy(...) }` to reduce state,
  `getCurrentState()` to read it.
- Each screen with logic has a **contract file** (`HomeScreenContract.kt`) declaring an
  abstract ViewModel and its data-class ViewState. Implementations are `*ViewModelImpl`.
  Platform-dependent VMs live in the platform source set (e.g. `MainScreenViewModelImpl` in androidMain).
- The player VM (`MainScreenViewModel`) is created once in `App()` and passed down; never `koinViewModel()` it
  inside a NavHost destination (that would open a second player connection). Other screen VMs (Notes, journal
  editor, Sleep, Tracking, Meditation) live in commonMain and are resolved per destination with `koinViewModel()`.
- **Playback (Android)**: `PlaybackService` (a Media3 `MediaSessionService`) owns ExoPlayer, the playlist
  (built-ins + custom sounds, synced from the DB), the sleep timer + fade-out and the media notification.
  `MainScreenViewModelImpl` talks to it through a `MediaController`: custom `SessionCommand`s from `PlaybackContract`
  for play/timer, while the service publishes timer state as session extras (`TimerSnapshot`).
  `DozzzePlayer` (a `ForwardingSimpleBasePlayer`) makes next/previous wrap and puts the timer in the notification.
- **Use cases**: interface `XxxUseCase` + implementation named without suffix (`InsertCustomSound`), invoked as functions.
- **Repositories**: public interface + `internal class XxxRepositoryImpl`, returning `Flow` or `Result<T>`.
- **DI registration**:
  - Shared bindings → `storageModule` in `di/LocalModule.kt`.
  - Android bindings → `homeScreenAndroidModule` in `androidMain/.../di/HomeScreenAndroidModule.kt`.
  - Koin is started in `DozzzeApplication` with `sharedKoinModules + sharedAndroidKoinModules`.
- **Navigation**: add a `data object` to the sealed `Screens` class (`ui/screens/main/Screens.kt`) and a matching
  `composable(...)` in the `NavHost` in `MainScreen.kt`. Start destination is `Screens.Home`.
  `App()` shows `SplashScreen` first, then `MainScreen`.
- **Resources**: use Compose Resources (`Res.drawable.*`, `Res.font.*`) from `whitenoise.composeapp.generated.resources`.
  Bundled audio is Android `R.raw.*`, mapped by key (`rain`, `ocean`, `forest`, `thunder`) in `PlaybackService`.
- **Database changes**: edit the `.sq` files, then rebuild to regenerate code. Schema output goes to
  `src/commonMain/sqldelight/databases`. Changes to existing tables also need a new `migrations/N.sqm`
  (schema version is currently 4).
- UI components use the `Dozzze` prefix (`DozzzeButton`, `DozzzeTextField`, `DozzzeTheme`).
- Section banner comments (`// ||   SECTION   ||`) are used to organize large ViewModels; follow that style there.

## Platform status / known gaps

- **iOS does not run yet**: Koin is never started on iOS, there is no iOS `MainScreenViewModel` binding or settings
  store, no `WakeUpAlarm` / `BackgroundPlayback` implementations, and `rememberSoundPicker` in `nativeMain` is `TODO()`. `App()` calls `koinViewModel<MainScreenViewModel>()`,
  so it will crash on iOS until these exist.
- Auth and profile were removed (migration `2.sqm` drops the old `user` table).
- Many `Screens` entries reuse placeholder icons.
- Release builds use R8 (minify + resource shrinking). A newer R8 is pinned in the root `build.gradle.kts`
  because AGP 8.9's bundled R8 cannot read Kotlin 2.2 metadata.
- Bundled sounds in `res/raw` are 5–6 min AAC (.m4a) loops with the seam crossfaded in; the MP4 edit list
  makes ExoPlayer loop them gaplessly. Replace them the same way (keep names) rather than adding long mp3s.

## Git

- Main branch: `master`. Commit messages are prefixed with the scope: `[SHARED]`, `[ANDROID]` (and `[IOS]` for iOS work).
