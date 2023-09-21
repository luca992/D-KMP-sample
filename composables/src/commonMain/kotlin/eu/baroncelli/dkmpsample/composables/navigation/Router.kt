package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.unit.dp
import eu.baroncelli.dkmpsample.composables.navigation.templates.OnePane
import eu.baroncelli.dkmpsample.composables.navigation.templates.TwoPane
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation

@Composable
fun Navigation.Router() {

    val screenUIsStateHolder = rememberSaveableStateHolder()
    screenStackToNavigationState =
        remember { mutableStateMapOf(*screenStackToNavigationState.entries.map { it.toPair() }.toTypedArray()) }

    val twopaneWidthThreshold = 1000.dp
    BoxWithConstraints {
        if (maxWidth < maxHeight || maxWidth < twopaneWidthThreshold) {
            OnePane(screenUIsStateHolder)
        } else {
            TwoPane(screenUIsStateHolder)
        }
    }

    HandleBackButton(screenUIsStateHolder)

}
