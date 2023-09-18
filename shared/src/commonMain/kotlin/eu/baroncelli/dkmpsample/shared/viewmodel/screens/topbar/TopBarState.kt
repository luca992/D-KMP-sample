package eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar

import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenState

// here is the data class defining the state for this screen
data class TopBarState(
    val title: String,
    val inLevel1MainStackScreen: Boolean = true,
) : ScreenState
