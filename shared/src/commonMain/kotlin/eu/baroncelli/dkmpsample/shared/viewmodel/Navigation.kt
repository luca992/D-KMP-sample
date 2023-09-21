package eu.baroncelli.dkmpsample.shared.viewmodel

import eu.baroncelli.dkmpsample.shared.viewmodel.screens.CallOnInitValues
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.navigationSettings
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.topbar.setTopBarTitleAndNavState
import kotlinx.coroutines.Job

data class NavigationState(
    val currentLevel1ScreenIdentifier: ScreenIdentifier,
    var paths: MutableMap<String, MutableList<ScreenIdentifier>> = mutableMapOf(),
    // in paths: key is the level1ScreenIdentifier URI, value is the vertical backstack without the level1ScreenIdentifier
    val nextBackQuitsApp: Boolean
) {
    val currentPath: MutableList<ScreenIdentifier>
        get() = paths[currentLevel1ScreenIdentifier.URI]!!
    val topScreenIdentifier: ScreenIdentifier
        get() = if (currentPath.isEmpty()) currentLevel1ScreenIdentifier else currentPath.last()
}


class Navigation(val stateManager: StateManager) {

    val stateProvider by lazy { StateProvider(stateManager) }

    var screenStackToNavigationState: MutableMap<ScreenStack, NavigationState> = getStartNavigationStates()

    init {
        // todo: maybe just set screenStackToNavigationState to an empty map instead of using getStartNavigationStates()
        ScreenStack.entries.forEach { screenStack ->
            val screenIdentifier = getStartScreenIdentifier(screenStack)
            selectLevel1Navigation(screenStack, screenIdentifier)
        }
    }

    val nextBackQuitsApp: Boolean
        get() = stateManager.screenStackToLevel1Backstack.size + stateManager.screenStackToCurrentVerticalBackstack.size == 2

    private fun savedLevel1URI(screenStack: ScreenStack) =
        stateManager.dataRepository.localSettings.getSavedScreenStackLevel1URI(screenStack)

    private fun saveLevel1URI(screenStack: ScreenStack, uri: URI) =
        stateManager.dataRepository.localSettings.setSavedScreenStackLevel1URI(screenStack, uri)

    fun getStartNavigationStates(): MutableMap<ScreenStack, NavigationState> {
        return ScreenStack.entries.associateWith { screenStack ->
            val screenIdentifier = getStartScreenIdentifier(screenStack)
//            selectLevel1Navigation(screenStack, screenIdentifier)
            NavigationState(
                screenIdentifier,
                mutableMapOf(screenIdentifier.URI to mutableListOf()),
                nextBackQuitsApp
            )
        }.toMutableMap()
    }

    fun getStartScreenIdentifier(screenStack: ScreenStack): ScreenIdentifier {
        var startScreenIdentifier =
            navigationSettings.screenStackDefaultStartScreen(screenStack).screenIdentifier
        if (navigationSettings.saveLastLevel1Screen) {
            startScreenIdentifier = try {
                ScreenIdentifier.getByURI(savedLevel1URI(screenStack)) ?: startScreenIdentifier
            } catch (e: ScreenParamsDeserializationException) {
                debugLogger.i(
                    "Warning: Failed to deserialize params for screen: ${startScreenIdentifier.screen.asString}. " +
                            "Retrying with default params for screen"
                )
                stateManager.dataRepository.localSettings.clear()
                ScreenIdentifier.getByURI(savedLevel1URI(screenStack))!!
            }
        }
        return startScreenIdentifier
    }

    fun updateNavigationState(screenStack: ScreenStack) {
        screenStackToNavigationState[screenStack] = NavigationState(
            currentLevel1ScreenIdentifier = stateManager.currentLevel1ScreenIdentifier(screenStack)!!,
            paths = getPaths(screenStack),
            nextBackQuitsApp = nextBackQuitsApp
        )
        val topScreenIdentifier = screenStackToNavigationState[screenStack]!!.topScreenIdentifier
        if (navigationSettings.saveLastLevel1Screen &&
            topScreenIdentifier.screen.navigationLevel == 1
        ) {
            saveLevel1URI(screenStack, topScreenIdentifier.URI)
        }
        stateManager.events.setTopBarTitleAndNavState(
            topScreenIdentifier.getScreenInitSettings(stateManager).title,
            topScreenIdentifier.screen.navigationLevel == 1
        )
        debugLogger.i("UI NAVIGATION RECOMPOSITION: topScreenIdentifier URI -> " + screenStackToNavigationState[screenStack]!!.topScreenIdentifier.URI)
    }

    fun getPaths(screenStack: ScreenStack): MutableMap<String, MutableList<ScreenIdentifier>> {
        val paths = mutableMapOf<String, MutableList<ScreenIdentifier>>()
        stateManager.screenStackToVerticalNavigationLevels[screenStack]!!.values.forEach { levelsMap ->
            val path = mutableListOf<ScreenIdentifier>()
            levelsMap.keys.sorted().forEach {
                if (it > 1) {
                    path.add(levelsMap[it]!!)
                }
            }
            paths[levelsMap[1]!!.URI] = path
        }
        return paths
    }


    // NAVIGATION FUNCTIONS

    // this function is called from iOS, and it calls the proper "navigateToScreen"
    // only if it's the destination screenIdentifier is valid
    fun navigateToScreenForIos(
        screenStack: ScreenStack,
        screenIdentifier: ScreenIdentifier,
        level1ScreenIdentifier: ScreenIdentifier
    ) {
        //debugLogger.i("navigateToScreenForIos: "+screenIdentifier.URI+" / level1ScreenIdentifier: "+level1ScreenIdentifier.URI)
        if (level1ScreenIdentifier.URI != stateManager.currentLevel1ScreenIdentifier(screenStack)?.URI ||
            stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.any { it.URI == screenIdentifier.URI }
        ) {
            //debugLogger.i("navigateToScreenForIos: BLOCKED / shared side currentVerticalBackstack: "+stateManager.currentVerticalBackstack.map { it.URI })
            return
        }
        navigateToScreen(screenStack, screenIdentifier)
    }

    fun navigateToScreen(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier) {
        debugLogger.i("navigate -> " + screenIdentifier.URI)
        addScreenToBackstack(screenStack, screenIdentifier)
        updateNavigationState(screenStack)
    }


    fun selectLevel1Navigation(screenStack: ScreenStack, level1ScreenIdentifier: ScreenIdentifier) {
        debugLogger.i("selectLevel1Navigation -> " + level1ScreenIdentifier.URI)
        cleanCurrentVerticalBackstacks(screenStack)
        stateManager.screenStackToLevel1Backstack[screenStack]!!.removeAll { it.URI == level1ScreenIdentifier.URI }
        if (navigationSettings.alwaysQuitOnHomeScreen) {
            if (level1ScreenIdentifier.URI == navigationSettings.screenStackDefaultStartScreen(screenStack).screenIdentifier.URI) {
                stateManager.screenStackToLevel1Backstack[screenStack]!!.clear() // remove all elements
            } else if (stateManager.screenStackToLevel1Backstack.size == 0) {
                stateManager.screenStackToLevel1Backstack[screenStack]!!.add(
                    navigationSettings.screenStackDefaultStartScreen(
                        screenStack
                    ).screenIdentifier
                )
            }
        }
        stateManager.screenStackToLevel1Backstack[screenStack]!!.add(level1ScreenIdentifier)
        if (stateManager.currentVerticalNavigationLevelsMap(screenStack).isEmpty()) {
            stateManager.screenStackToVerticalNavigationLevels[screenStack]!![level1ScreenIdentifier.URI] =
                mutableMapOf()
            addScreenToBackstack(screenStack, level1ScreenIdentifier)
        } else {
            stateManager.currentVerticalNavigationLevelsMap(screenStack).values.sortedBy { it.screen.navigationLevel }
                .forEach {
                    addScreenToBackstack(screenStack, it)
                }
        }
        updateNavigationState(screenStack)
    }

    fun cleanCurrentVerticalBackstacks(screenStack: ScreenStack) {
        // clean current vertical backstack and remove unneeded screens, based on level1VerticalBackstackEnabled()
        if (stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.isNotEmpty()) {
            stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.filter {
                if (stateManager.screenStackToCurrentVerticalBackstack[screenStack]!![0].level1VerticalBackstackEnabled()) {
                    it.URI !in stateManager.screenStackToVerticalNavigationLevels[screenStack]!![stateManager.screenStackToCurrentVerticalBackstack[screenStack]!![0].URI]!!.values.map { levelScreen -> levelScreen.URI }
                } else {
                    it.screen.navigationLevel > 1
                }
            }.forEach {
                stateManager.removeScreen(screenStack, it)
            }
            if (!stateManager.screenStackToCurrentVerticalBackstack[screenStack]!![0].level1VerticalBackstackEnabled()) {
                stateManager.screenStackToVerticalNavigationLevels[screenStack]!![stateManager.screenStackToCurrentVerticalBackstack[screenStack]!![0].URI]!!.keys.removeAll { it != 1 }
            }
        }
        stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.clear()
    }


    // ADD SCREEN TO BACKSTACK

    fun addScreenToBackstack(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): Job? {
        debugLogger.i("addScreenToBackstack: " + screenIdentifier.URI)
        stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.add(screenIdentifier)
        stateManager.currentVerticalNavigationLevelsMap(screenStack)[screenIdentifier.screen.navigationLevel] =
            screenIdentifier
        return stateManager.initScreen(screenStack, screenIdentifier)
    }


    // EXIT SCREEN

    // this function is called from iOS, and it calls the proper "exitScreen"
    // only if the screenIdentifier to exit is valid
    fun exitScreenForIos(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier) {
        //debugLogger.i("exitScreenForIos: " + screenIdentifier.URI)
        if (screenIdentifier.URI != stateManager.currentScreenIdentifier(screenStack).URI || nextBackQuitsApp) {
            return
        }
        exitScreen(screenStack, screenIdentifier)
    }

    fun exitScreen(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier) {
        debugLogger.i("exitScreen: " + screenIdentifier.URI)
        if (screenIdentifier.screen.navigationLevel == 1) {
            stateManager.screenStackToLevel1Backstack[screenStack]!!.removeLast()
            stateManager.screenStackToVerticalNavigationLevels[screenStack]!!.remove(screenIdentifier.URI)
            stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.clear()
            stateManager.removeScreen(screenStack, screenIdentifier)
            stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.add(
                stateManager.currentLevel1ScreenIdentifier(
                    screenStack
                )!!
            )
            if (!stateManager.isInTheStatesMap(
                    screenStack,
                    stateManager.currentLevel1ScreenIdentifier(screenStack)!!
                )
            ) {
                stateManager.screenStackToVerticalNavigationLevels[screenStack]!![stateManager.currentLevel1ScreenIdentifier(
                    screenStack
                )!!.URI] =
                    mutableMapOf(1 to stateManager.currentLevel1ScreenIdentifier(screenStack)!!)
                stateManager.initScreen(screenStack, stateManager.screenStackToLevel1Backstack[screenStack]!!.last())
            }
        } else {
            stateManager.currentVerticalNavigationLevelsMap(screenStack).remove(screenIdentifier.screen.navigationLevel)
            stateManager.screenStackToCurrentVerticalBackstack[screenStack]!!.removeAll { it.URI == screenIdentifier.URI }
            stateManager.currentVerticalNavigationLevelsMap(screenStack)[stateManager.currentScreenIdentifier(
                screenStack
            ).screen.navigationLevel] =
                stateManager.currentScreenIdentifier(screenStack) // set new currentScreenIdentifier, after the removal
            if (!isInAnyVerticalNavigationLevel(screenStack, screenIdentifier)) {
                stateManager.removeScreen(screenStack, screenIdentifier)
            }
        }
        val newScreenInitSettings =
            stateManager.currentScreenIdentifier(screenStack).getScreenInitSettings(stateManager)
        if (newScreenInitSettings.callOnInitAtEachNavigation != CallOnInitValues.DONT_CALL) {
            stateManager.runCallOnInit(
                screenStack,
                stateManager.currentScreenIdentifier(screenStack),
                newScreenInitSettings
            )
        }
        updateNavigationState(screenStack)
    }

    fun isInAnyVerticalNavigationLevel(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): Boolean {
        stateManager.screenStackToVerticalNavigationLevels[screenStack]!!.forEach { verticalNavigation ->
            verticalNavigation.value.forEach {
                if (it.value.URI == screenIdentifier.URI) {
                    return true
                }
            }
        }
        return false
    }


    fun onReEnterForeground() {
        // not called at app startup, but only when reentering the app after it was in background
        debugLogger.i("onReEnterForeground: recomposition is triggered")
        val reinitializedScreens = stateManager.reinitScreenScopes()
        reinitializedScreens.flatMap { (screenStack, screenIds) ->
            screenIds.map { screenId ->
                screenStack to screenId
            }
        }.forEach { (screenStack, screenId) ->
            screenId.getScreenInitSettings(stateManager).apply {
                if (callOnInitAlsoAfterBackground) {
                    stateManager.runCallOnInit(screenStack, screenId, this)
                }
            }
        }
    }

    fun onEnterBackground() {
        debugLogger.i("onEnterBackground: screen scopes are cancelled")
        stateManager.cancelScreenScopes()
    }

}