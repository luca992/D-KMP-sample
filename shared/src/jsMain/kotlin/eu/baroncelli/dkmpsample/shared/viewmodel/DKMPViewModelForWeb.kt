package eu.baroncelli.dkmpsample.shared.viewmodel


import app.cash.sqldelight.driver.worker.WebWorkerDriver
import eu.baroncelli.dkmpsample.shared.datalayer.Repository
import mylocal.db.LocalDb
import org.w3c.dom.Worker


suspend fun DKMPViewModel.Factory.getWebInstance(): DKMPViewModel {
    val sqlDriver = WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
        )
    )
    LocalDb.Schema.create(sqlDriver).await()
    val repository = Repository(sqlDriver)
    return DKMPViewModel(repository)
}