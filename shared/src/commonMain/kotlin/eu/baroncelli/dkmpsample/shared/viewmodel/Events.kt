package eu.baroncelli.dkmpsample.shared.viewmodel

import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack


class Events(val stateManager: StateManager) {

    val dataRepository
        get() = stateManager.dataRepository

    // we run each event function on a Dispatchers.Main coroutine
    fun screenCoroutine(screenStack: ScreenStack, block: suspend () -> Unit) {
        debugLogger.i("/$screenStack:${stateManager.currentScreenIdentifier(screenStack).URI}: an Event is called")
        stateManager.runInScreenScope(screenStack) { block() }
    }

}