package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation

@Composable
actual fun Navigation.HandleBackButton(
    saveableStateHolder: SaveableStateHolder
) {
    // there is no physical back button to handle on desktop
}