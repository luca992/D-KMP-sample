package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateMap
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
expect fun Navigation.HandleBackButton(
    saveableStateHolder: SaveableStateHolder,
    localNavigationState: SnapshotStateMap<ScreenStack, NavigationState>,
)

fun Navigation.onBackPressed(
    saveableStateHolder: SaveableStateHolder,
    localNavigationState: SnapshotStateMap<ScreenStack, NavigationState>,
) {
    val navState = localNavigationState[ScreenStack.Main]!!
    val originScreenIdentifier = navState.topScreenIdentifier
    exitScreen(ScreenStack.Main, originScreenIdentifier) // shared navigationState is updated
    localNavigationState[ScreenStack.Main] = screenStackToNavigationState[ScreenStack.Main]!! // update localNavigationState
    saveableStateHolder.removeState(originScreenIdentifier)
}