package eu.baroncelli.dkmpsample.shared.viewmodel.screens

import eu.baroncelli.dkmpsample.shared.viewmodel.ScreenIdentifier
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.Screen.CountriesList
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.Screen.TopBar
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrieslist.CountriesListType.ALL
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.countrieslist.CountriesListType.FAVORITES


// CONFIGURATION SETTINGS

object navigationSettings {
    // the start screens for each screen stack should be specified here
    fun screenStackDefaultStartScreen(stack: ScreenStack) =
        when (stack) {
            ScreenStack.Main -> Level1Navigation.AllCountries
            ScreenStack.TopBar -> Level1Navigation.TopBarL1Nav
        }

    val saveLastLevel1Screen = true
    val alwaysQuitOnHomeScreen = true
}


// LEVEL 1 NAVIGATION OF THE APP

enum class Level1Navigation(val screenIdentifier: ScreenIdentifier, val rememberVerticalStack: Boolean = false) {
    AllCountries(ScreenIdentifier.get(CountriesList, CountriesListParams(listType = ALL)), true),
    FavoriteCountries(ScreenIdentifier.get(CountriesList, CountriesListParams(listType = FAVORITES)), true),
    TopBarL1Nav(ScreenIdentifier.get(TopBar, null), true),
}