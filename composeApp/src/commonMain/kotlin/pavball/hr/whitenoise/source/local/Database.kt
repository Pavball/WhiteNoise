package pavball.hr.whitenoise.source.local

import pavball.hr.whitenoise.db.AppDatabase


internal class Database(databaseDriverFactory: DriverFactory) {

    private val database by lazy { AppDatabase(databaseDriverFactory.createDriver()) }

    val queries by lazy { database.customSoundQueries }
}