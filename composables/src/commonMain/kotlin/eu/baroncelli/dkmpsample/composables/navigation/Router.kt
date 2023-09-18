package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.dp
import eu.baroncelli.dkmpsample.composables.navigation.templates.OnePane
import eu.baroncelli.dkmpsample.composables.navigation.templates.TwoPane
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenIdentifier
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.Level1Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.Screen
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenParams
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack

@Composable
fun Navigation.Router() {

    val screenUIsStateHolder = rememberSaveableStateHolder()
    val screenStackToLocalNavigationState =
        remember { mutableStateMapOf(*screenStackToNavigationState.entries.map { it.toPair() }.toTypedArray()) }

    val twopaneWidthThreshold = 1000.dp
    BoxWithConstraints {
        if (maxWidth < maxHeight || maxWidth < twopaneWidthThreshold) {
            OnePane(screenUIsStateHolder, screenStackToLocalNavigationState)
        } else {
            TwoPane(screenUIsStateHolder, screenStackToLocalNavigationState)
        }
    }

    HandleBackButton(screenUIsStateHolder, screenStackToLocalNavigationState)

}

fun Navigation.navigationProcessor(
    screenStack: ScreenStack,
    localNavigationState: SnapshotStateMap<ScreenStack, NavigationState>
): (Screen, ScreenParams?) -> Unit {
    return { screen, screenParams ->
        val screenIdentifier = ScreenIdentifier.get(screen, screenParams)
        navigateToScreen(screenStack, screenIdentifier) // shared navigationState is updated
        localNavigationState[screenStack] = screenStackToNavigationState[screenStack]!! // update localNavigationState
    }
}

fun Navigation.level1NavigationProcessor(
    screenStack: ScreenStack,
    localNavigationState: SnapshotStateMap<ScreenStack, NavigationState>
): (Level1Navigation) -> Unit {
    return {
        selectLevel1Navigation(screenStack, it.screenIdentifier) // shared navigationState is updated
        localNavigationState[screenStack] = screenStackToNavigationState[screenStack]!! // update localNavigationState
    }
}