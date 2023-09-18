package eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrieslist

import eu.baroncelli.dkmpsample.shared.datalayer.functions.getFavoriteCountriesMap
import eu.baroncelli.dkmpsample.shared.viewmodel.Events
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack


/********** EVENT functions, called directly by the UI layer **********/

fun Events.selectFavorite(countryName: String) = screenCoroutine(ScreenStack.Main) {
    val favorites = dataRepository.getFavoriteCountriesMap(alsoToggleCountry = countryName)
    // update state with new favorites map, after toggling the value for the specified country
    stateManager.updateScreen(ScreenStack.Main, CountriesListState::class) {
        it.copy(favoriteCountries = favorites)
    }
}
