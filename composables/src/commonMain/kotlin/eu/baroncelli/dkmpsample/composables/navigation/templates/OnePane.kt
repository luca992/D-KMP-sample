package eu.baroncelli.dkmpsample.composables.navigation.templates

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateMap
import eu.baroncelli.dkmpsample.composables.navigation.ScreenPicker
import eu.baroncelli.dkmpsample.composables.navigation.bars.Level1BottomBar
import eu.baroncelli.dkmpsample.composables.navigation.onBackPressed
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
fun Navigation.OnePane(
    saveableStateHolder: SaveableStateHolder,
) {
    val mainStackScreenIdentifier = screenStackToNavigationState[ScreenStack.Main]!!.topScreenIdentifier
    val topBarStackScreenIdentifier = screenStackToNavigationState[ScreenStack.TopBar]!!.topScreenIdentifier
    Scaffold(
        topBar = {
            saveableStateHolder.SaveableStateProvider(topBarStackScreenIdentifier.URI) {
                ScreenPicker(
                    ScreenStack.TopBar,
                    topBarStackScreenIdentifier
                ) {
                    onBackPressed(saveableStateHolder)
                }
            }
        },
        content = {
            saveableStateHolder.SaveableStateProvider(mainStackScreenIdentifier.URI) {
                ScreenPicker(ScreenStack.Main, mainStackScreenIdentifier)
            }
        },
        bottomBar = {
            if (mainStackScreenIdentifier.screen.navigationLevel == 1) Level1BottomBar(mainStackScreenIdentifier)
        }
    )
}