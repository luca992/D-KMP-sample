package eu.baroncelli.dkmpsample.shared.datalayer.sources.localsettings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.long
import com.russhwolf.settings.set
import eu.baroncelli.dkmpsample.shared.viewmodel.URI
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.navigationSettings

class MySettings(private val s: Settings) {


    // here we define all our local settings properties,
    // by using the MultiplatformSettings library delegated properties

    var listCacheTimestamp by s.long(defaultValue = 0)

    fun getSavedScreenStackLevel1URI(screenStack: ScreenStack) = s.getString(
        "saved${screenStack}ScreenStackLevel1URI",
        navigationSettings.screenStackDefaultStartScreen(screenStack).screenIdentifier.URI
    )

    fun setSavedScreenStackLevel1URI(screenStack: ScreenStack, uri: URI) {
        s["saved${screenStack}ScreenStackLevel1URI"] =
            navigationSettings.screenStackDefaultStartScreen(screenStack).screenIdentifier.URI
    }


    fun clear() = s.clear()
}