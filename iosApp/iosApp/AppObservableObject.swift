//
//  AppObservableObject.swift
//
//  Created by Daniele Baroncelli on 13/03/2021.
//
//

import SwiftUI
import shared

class AppObservableObject: ObservableObject {
    let model : DKMPViewModel = DKMPViewModel.Factory().getIosInstance()
    var screenStates: [ScreenIdentifier:ObservableScreenState] = [:]
    var dkmpNav : Navigation {
        return self.model.navigation
    }
    @Published var localNavigationState : NavigationState
    


    init() {
        // "getDefaultAppState" and "onChange" are iOS-only DKMPViewModel's extension functions, defined in shared/iosMain
        self.localNavigationState = model.navigation.navigationState
    }

    func getObservableScreenState(screenIdentifier: ScreenIdentifier) -> ObservableScreenState {
        if screenStates[screenIdentifier] == nil{
            screenStates[screenIdentifier] = ObservableScreenState(requestedSId: screenIdentifier, stateProvider: dkmpNav.stateProvider)
        }
        return screenStates[screenIdentifier]!
    }

}
