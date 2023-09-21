package eu.baroncelli.dkmpsample.composables.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import eu.baroncelli.dkmpsample.composables.screens.countrieslist.CountriesListScreen
import eu.baroncelli.dkmpsample.composables.screens.countrieslist.CountriesListTwoPaneDefaultDetail
import eu.baroncelli.dkmpsample.composables.screens.countrydetail.CountryDetailScreen
import eu.baroncelli.dkmpsample.composables.screens.topbar.TopBar
import eu.baroncelli.dkmpsample.shared.viewmodel.Navigation
import eu.baroncelli.dkmpsample.shared.viewmodel.NavigationState
import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenIdentifier
import eu.baroncelli.dkmpsample.shared.viewmodel.debugLogger
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.CountryDetailParams
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.Screen.*
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrieslist.CountriesListState
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrieslist.selectFavorite
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrydetail.CountryDetailState
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar.TopBarState


@Composable
fun Navigation.ScreenPicker(
    screenStack: ScreenStack,
    screenIdentifier: ScreenIdentifier,
    onBackPressed: (() -> Unit)? = null
) {
    when (screenIdentifier.screen) {

        TopBar -> {
            val state by stateProvider.get<TopBarState>(screenStack, screenIdentifier).collectAsState()
            TopBar(
                state = state,
                onClickNavigateUp = onBackPressed ?: { debugLogger.i("No onBackPressed handler set") })
        }

        CountriesList -> {
            val state by stateProvider.get<CountriesListState>(screenStack, screenIdentifier).collectAsState()
            CountriesListScreen(
                countriesListState = state,
                onListItemClick = {
                    navigateToScreen(
                        ScreenStack.Main,
                        ScreenIdentifier.get(CountryDetail, CountryDetailParams(countryName = it))
                    )
                },
                onFavoriteIconClick = { stateManager.events.selectFavorite(countryName = it) },
            )
        }

        CountryDetail -> {
            val state by stateProvider.get<CountryDetailState>(screenStack, screenIdentifier).collectAsState()
            CountryDetailScreen(
                countryDetailState = state
            )
        }

        else ->
            return

    }

}


@Composable
fun Navigation.TwoPaneDefaultDetail(
    screenIdentifier: ScreenIdentifier
) {

    when (screenIdentifier.screen) {

        CountriesList ->
            CountriesListTwoPaneDefaultDetail()

        else -> Box {}
    }

}