package pavball.hr.whitenoise

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pavball.hr.whitenoise.di.sharedAndroidKoinModules
import pavball.hr.whitenoise.di.sharedKoinModules

class DozzzeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Started here (not in MainActivity) so the playback service can use Koin
        // even without an activity, and singletons hold the app context, not an activity.
        startKoin {
            androidContext(this@DozzzeApplication)
            modules(sharedKoinModules + sharedAndroidKoinModules)
        }
    }
}
