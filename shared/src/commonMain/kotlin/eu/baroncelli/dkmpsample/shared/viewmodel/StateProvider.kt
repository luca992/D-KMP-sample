package eu.baroncelli.dkmpsample.shared.viewmodel

import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StateProvider(val stateManager: StateManager) {

    inline fun <reified T : ScreenState> get(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): StateFlow<T> {
        return getScreenState(screenStack, screenIdentifier)
    }

    // reified functions cannot be exported to iOS, so we use this function returning the "ScreenState" interface type
    // on Swift, we then need to cast it to the specific state class
    fun getToCast(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): StateFlow<ScreenState> {
        return getScreenState(screenStack, screenIdentifier)
    }

    inline fun <reified T : ScreenState> getScreenState(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): StateFlow<T> {
        //debugLogger.i("getScreenState: "+screenIdentifier.URI)
        return stateManager.screenStackToScreenStatesMap[screenStack]!![screenIdentifier.URI]!!.asStateFlow() as StateFlow<T>
    }

}



