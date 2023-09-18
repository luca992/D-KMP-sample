package eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar

import eu.baroncelli.dkmpsample.shared.viewmodel.Events
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack


/********** NO EVENT FUNCTION IS DEFINED ON THIS SCREEN **********/

fun Events.setTopBarTitleAndNavState(title: String, inLevel1MainStackScreen: Boolean) {
    stateManager.updateScreen(ScreenStack.TopBar, TopBarState::class) {
        it.copy(
            title = title,
            inLevel1MainStackScreen = inLevel1MainStackScreen
        )
    }
}
