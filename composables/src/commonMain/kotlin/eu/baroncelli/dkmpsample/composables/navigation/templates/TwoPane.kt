package eu.baroncelli.dkmpsample.composables.navigation.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.baroncelli.dkmpsample.composables.navigation.ScreenPicker
import eu.baroncelli.dkmpsample.composables.navigation.TwoPaneDefaultDetail
import eu.baroncelli.dkmpsample.composables.navigation.bars.Level1NavigationRail
import eu.baroncelli.dkmpsample.composables.navigation.onBackPressed
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenIdentifier
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
fun Navigation.TwoPane(
    saveableStateHolder: SaveableStateHolder,
    screenStackToLocalNavigationState: SnapshotStateMap<ScreenStack, NavigationState>
) {
    val topBarStackScreenIdentifier = screenStackToLocalNavigationState[ScreenStack.TopBar]!!.topScreenIdentifier
    val masterScreenIdentifier = twoPaneMasterScreen(screenStackToLocalNavigationState)
    val detailScreenIdentifier = twoPaneDetailScreen(screenStackToLocalNavigationState)
    Scaffold(
        topBar = {
            saveableStateHolder.SaveableStateProvider(topBarStackScreenIdentifier.URI) {
                ScreenPicker(
                    ScreenStack.TopBar,
                    topBarStackScreenIdentifier,
                ) {
                    onBackPressed(saveableStateHolder, screenStackToLocalNavigationState)
                }
            }
        },
        content = {
            Row {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                ) {
                    Level1NavigationRail(masterScreenIdentifier)
                }
                Column(
                    Modifier
                        .weight(0.4f)
                ) {
                    saveableStateHolder.SaveableStateProvider(masterScreenIdentifier.URI) {
                        ScreenPicker(
                            ScreenStack.Main,
                            masterScreenIdentifier,
                        )
                    }
                }
                Column(
                    Modifier
                        .weight(0.6f)
                        .padding(20.dp)
                ) {
                    if (detailScreenIdentifier == null) {
                        TwoPaneDefaultDetail(masterScreenIdentifier)
                    } else {
                        saveableStateHolder.SaveableStateProvider(detailScreenIdentifier.URI) {
                            ScreenPicker(
                                ScreenStack.Main,
                                detailScreenIdentifier,
                            )
                        }
                    }
                }
            }
        }
    )
}


fun Navigation.twoPaneMasterScreen(navState: SnapshotStateMap<ScreenStack, NavigationState>): ScreenIdentifier {
    return navState[ScreenStack.Main]!!.currentLevel1ScreenIdentifier
}

fun Navigation.twoPaneDetailScreen(navState: SnapshotStateMap<ScreenStack, NavigationState>): ScreenIdentifier? {
    return if (navState[ScreenStack.Main]!!.topScreenIdentifier.screen.navigationLevel > 1) {
        navState[ScreenStack.Main]!!.topScreenIdentifier
    } else {
        null
    }
}