package eu.baroncelli.dkmpsample.shared

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.MapSettings
import eu.baroncelli.dkmpsample.shared.datalayer.Repository
import mylocal.db.LocalDb

actual suspend fun getTestRepository(): Repository {
    val sqlDriver = NativeSqliteDriver(LocalDb.Schema.synchronous(), "test.db")
    return Repository(sqlDriver, MapSettings())
}