package eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar

import eu.baroncelli.dkmpsample.shared.viewmodel.StateManager
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenInitSettings


// INITIALIZATION settings for this screen
// to understand the initialization behaviour, read the comments in the ScreenInitSettings.kt file


fun StateManager.initTopBar() = ScreenInitSettings(
    title = /*params.name ?:*/ "",
    initState = {
        TopBarState(
            title = "Top Bar",
        )
    },
    callOnInit = {
    },
)