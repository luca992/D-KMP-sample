package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
expect fun Navigation.HandleBackButton(
    saveableStateHolder: SaveableStateHolder,
)

fun Navigation.onBackPressed(
    saveableStateHolder: SaveableStateHolder,
) {
    val navState = screenStackToNavigationState[ScreenStack.Main]!!
    val originScreenIdentifier = navState.topScreenIdentifier
    exitScreen(ScreenStack.Main, originScreenIdentifier)
    saveableStateHolder.removeState(originScreenIdentifier)
}