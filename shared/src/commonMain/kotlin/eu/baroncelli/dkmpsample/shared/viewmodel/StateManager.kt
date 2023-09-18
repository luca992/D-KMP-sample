package eu.baroncelli.dkmpsample.shared.viewmodel

import eu.baroncelli.dkmpsample.shared.datalayer.Repository
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.CallOnInitValues
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenInitSettings
import eu.baroncelli.dkmpsample.shared.viewmodel.screens.ScreenStack
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass


interface ScreenState

class StateManager(repo: Repository) {

    // for each ScreenStack a map of screen states currently in memory
    val screenStackToScreenStatesMap: Map<ScreenStack, MutableMap<URI, MutableStateFlow<ScreenState>>> =
        ScreenStack.entries.associateWith { mutableMapOf() }

    // for each ScreenStack a map of coroutine scopes associated to current screen states
    val screenStackToScreenScopesMap: Map<ScreenStack, MutableMap<URI, CoroutineScope>> =
        ScreenStack.entries.associateWith { mutableMapOf() }

    // for each ScreenStack a list elements which are only NavigationLevel1 screenIdentifiers
    val screenStackToLevel1Backstack: Map<ScreenStack, MutableList<ScreenIdentifier>> =
        ScreenStack.entries.associateWith { mutableListOf() }

    // list elements for each ScreenStack are the screenIdentifiers of the current vertical backstack
    val screenStackToCurrentVerticalBackstack: Map<ScreenStack, MutableList<ScreenIdentifier>> =
        ScreenStack.entries.associateWith { mutableListOf() }

    // for each screen stack:
    // the first map key is the NavigationLevel1 screenIdentifier URI, the second map key is the NavigationLevel numbers
    val screenStackToVerticalNavigationLevels: Map<ScreenStack, MutableMap<URI, MutableMap<Int, ScreenIdentifier>>> =
        ScreenStack.entries.associateWith { mutableMapOf() }

    fun currentScreenIdentifier(screenStack: ScreenStack): ScreenIdentifier =
        screenStackToCurrentVerticalBackstack[screenStack]!!.last()

    fun currentLevel1ScreenIdentifier(screenStack: ScreenStack): ScreenIdentifier? =
        screenStackToLevel1Backstack[screenStack]?.lastOrNull()

    fun currentVerticalNavigationLevelsMap(screenStack: ScreenStack): MutableMap<Int, ScreenIdentifier> =
        screenStackToVerticalNavigationLevels[screenStack]?.get(currentLevel1ScreenIdentifier(screenStack)?.URI)
            ?: mutableMapOf()

    internal val dataRepository by lazy { repo }
    val events by lazy { Events(this) }


    // INIT SCREEN

    fun initScreen(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): Job? {
        debugLogger.i("initScreen: " + screenIdentifier.URI)
        val screenInitSettings = screenIdentifier.getScreenInitSettings(this)
        if (screenStackToScreenScopesMap[screenStack]?.get(screenIdentifier.URI) == null || !screenStackToScreenScopesMap[screenStack]!![screenIdentifier.URI]!!.isActive) {
            screenStackToScreenScopesMap[screenStack]!![screenIdentifier.URI]?.cancel()
            screenStackToScreenScopesMap[screenStack]!![screenIdentifier.URI] = CoroutineScope(Job() + Dispatchers.Main)
        }
        var firstInit = false
        if (!isInTheStatesMap(screenStack, screenIdentifier)) {
            firstInit = true
            screenStackToScreenStatesMap[screenStack]!![screenIdentifier.URI] =
                MutableStateFlow(screenInitSettings.initState(screenIdentifier))
        } else if (screenInitSettings.callOnInitAtEachNavigation == CallOnInitValues.DONT_CALL) {
            return null  // in case: the state is already in the map
            //          AND "callOnInitAtEachNavigation" is set to DONT_CALL
            //      => we don't need to run the "callOnInit" function
        }
        return runCallOnInit(screenStack, screenIdentifier, screenInitSettings, firstInit)
    }

    fun isInTheStatesMap(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier): Boolean {
        return screenStackToScreenStatesMap[screenStack]!!.containsKey(screenIdentifier.URI)
    }

    fun runCallOnInit(
        screenStack: ScreenStack,
        screenIdentifier: ScreenIdentifier,
        screenInitSettings: ScreenInitSettings,
        firstInit: Boolean = false
    ): Job? {
        return runInScreenScope(screenStack, screenIdentifier) {
            screenInitSettings.callOnInit(this@StateManager)
        }
    }


    // UPDATE SCREEN

    inline fun <reified T : ScreenState> updateScreen(
        screenStack: ScreenStack,
        @Suppress("UNUSED_PARAMETER") stateClass: KClass<T>,
        update: (T) -> T,
    ) {
        debugLogger.i("updateScreen: " + stateClass.simpleName)
        //debugLogger.i("currentVerticalNavigationLevelsMap: "+currentVerticalNavigationLevelsMap.values.map { it.URI } )

        lateinit var screenIdentifier: ScreenIdentifier
        var screenState: T?
        for (i in currentVerticalNavigationLevelsMap(screenStack).keys.sortedDescending()) {
            screenState =
                screenStackToScreenStatesMap[screenStack]!![currentVerticalNavigationLevelsMap(screenStack)[i]?.URI]?.value as? T
            if (screenState != null) {
                screenIdentifier = currentVerticalNavigationLevelsMap(screenStack)[i]!!
                screenStackToScreenStatesMap[screenStack]!![screenIdentifier.URI]!!.value = update(screenState)
                debugLogger.i("state updated @ /${screenIdentifier.URI}")
                return
            }
        }
    }


    // REMOVE SCREEN

    fun removeScreen(screenStack: ScreenStack, screenIdentifier: ScreenIdentifier) {
        debugLogger.i("removeScreen: " + screenIdentifier.URI + " / level " + screenIdentifier.screen.navigationLevel)
        screenStackToScreenScopesMap[screenStack]!![screenIdentifier.URI]?.cancel() // cancel screen's coroutine scope
        screenStackToScreenScopesMap[screenStack]!!.remove(screenIdentifier.URI)
        val screenInitSettings = screenIdentifier.getScreenInitSettings(this)
        if (screenInitSettings.clearStateCacheWhenScreenIsRemovedFromBackstack) {
            debugLogger.i("removeState " + screenIdentifier.URI)
            screenStackToScreenStatesMap[screenStack]!!.remove(screenIdentifier.URI)
        }
    }


    // COROUTINE SCOPES FUNCTIONS

    fun reinitScreenScopes(): Map<ScreenStack, List<ScreenIdentifier>> {
        return ScreenStack.entries.associateWith { screenStack ->
            currentVerticalNavigationLevelsMap(screenStack).forEach {
                //debugLogger.i("reinitScreenScopes() "+it.value.URI)
                screenStackToScreenScopesMap[screenStack]!![it.value.URI] = CoroutineScope(Job() + Dispatchers.Main)
            }
            currentVerticalNavigationLevelsMap(screenStack).values.toMutableList() // return list of screens whose scope has been reinitialized
        }
    }

    // we run each event function on a Dispatchers.Main coroutine
    fun runInScreenScope(
        screenStack: ScreenStack,
        screenIdentifier: ScreenIdentifier? = null,
        block: suspend () -> Unit
    ): Job? {
        val URI = screenIdentifier?.URI ?: currentScreenIdentifier(screenStack).URI
        val screenScope = screenStackToScreenScopesMap[screenStack]!![URI]
        return screenScope?.launch {
            block()
        }
    }

    fun cancelScreenScopes() {
        screenStackToScreenScopesMap.flatMap { (screenStack, uriToScope) ->
            uriToScope.map { (uri, scope) ->
                Pair(Pair(screenStack, uri), scope)
            }
        }.toMap().forEach { (screenStackAndUri, scope) ->
            //debugLogger.i("cancelScreenScopes() "+it.key)
            scope.cancel() // cancel screen's coroutine scope
        }
    }

}
