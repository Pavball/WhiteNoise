package pavball.hr.whitenoise.source.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pavball.hr.whitenoise.db.AppDatabase

actual class DriverFactory(private val context: Context) {
    private var sqlDriver: SqlDriver? = null

    actual fun createDriver(): SqlDriver {
        if (sqlDriver == null) {
            sqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, "AppDatabase.db")
        }
        return sqlDriver!!
    }
}