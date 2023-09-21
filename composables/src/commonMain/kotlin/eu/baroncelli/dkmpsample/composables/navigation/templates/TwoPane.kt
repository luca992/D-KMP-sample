package eu.baroncelli.dkmpsample.composables.navigation.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.baroncelli.dkmpsample.composables.navigation.ScreenPicker
import eu.baroncelli.dkmpsample.composables.navigation.TwoPaneDefaultDetail
import eu.baroncelli.dkmpsample.composables.navigation.bars.Level1NavigationRail
import eu.baroncelli.dkmpsample.composables.navigation.onBackPressed
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenIdentifier
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
fun Navigation.TwoPane(
    saveableStateHolder: SaveableStateHolder,
) {
    val topBarStackScreenIdentifier = screenStackToNavigationState[ScreenStack.TopBar]!!.topScreenIdentifier
    val masterScreenIdentifier = twoPaneMasterScreen()
    val detailScreenIdentifier = twoPaneDetailScreen()
    Scaffold(
        topBar = {
            saveableStateHolder.SaveableStateProvider(topBarStackScreenIdentifier.URI) {
                ScreenPicker(
                    ScreenStack.TopBar,
                    topBarStackScreenIdentifier,
                ) {
                    onBackPressed(saveableStateHolder)
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


fun Navigation.twoPaneMasterScreen(): ScreenIdentifier {
    return screenStackToNavigationState[ScreenStack.Main]!!.currentLevel1ScreenIdentifier
}

fun Navigation.twoPaneDetailScreen(): ScreenIdentifier? {
    return if (screenStackToNavigationState[ScreenStack.Main]!!.topScreenIdentifier.screen.navigationLevel > 1) {
        screenStackToNavigationState[ScreenStack.Main]!!.topScreenIdentifier
    } else {
        null
    }
}