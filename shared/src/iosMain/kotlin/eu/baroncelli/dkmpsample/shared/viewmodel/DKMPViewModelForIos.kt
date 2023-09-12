package eu.baroncelli.dkmpsample.shared.viewmodel

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import eu.baroncelli.dkmpsample.shared.datalayer.Repository
import mylocal.db.LocalDb


fun DKMPViewModel.Factory.getIosInstance(): DKMPViewModel {
    val sqlDriver = NativeSqliteDriver(LocalDb.Schema.synchronous(), "Local.db")
    val repository = Repository(sqlDriver)
    return DKMPViewModel(repository)
}
