package eu.baroncelli.dkmpsample.composables.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateMap
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
actual fun Navigation.HandleBackButton(
    saveableStateHolder: SaveableStateHolder,
    localNavigationState: SnapshotStateMap<ScreenStack, NavigationState>
) {
    BackHandler(!localNavigationState[ScreenStack.Main]!!.nextBackQuitsApp) { // catching the back button
        onBackPressed(saveableStateHolder, localNavigationState)
    }
}