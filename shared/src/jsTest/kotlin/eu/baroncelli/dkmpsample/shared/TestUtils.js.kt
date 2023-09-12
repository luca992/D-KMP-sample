package eu.baroncelli.dkmpsample.shared

import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.russhwolf.settings.MapSettings
import eu.baroncelli.dkmpsample.shared.datalayer.Repository
import mylocal.db.LocalDb
import org.w3c.dom.Worker

actual suspend fun getTestRepository(): Repository {
    val sqlDriver = WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
        )
    )
    LocalDb.Schema.create(sqlDriver).await()
    return Repository(sqlDriver, MapSettings())
}