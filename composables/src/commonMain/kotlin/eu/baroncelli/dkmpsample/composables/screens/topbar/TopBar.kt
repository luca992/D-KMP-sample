package eu.baroncelli.dkmpsample.composables.screens.topbar


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar.TopBarState

@Composable
fun TopBar(
    state: TopBarState, onClickNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = state.title, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        },
        navigationIcon = if (!state.inLevel1MainStackScreen) {
            {
                IconButton(onClick = onClickNavigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        },
    )
}
