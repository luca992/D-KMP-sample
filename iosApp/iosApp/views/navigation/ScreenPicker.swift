//
//  ScreenPicker.swift
//
//  Created by Daniele Baroncelli on 06/05/2021.
//
//

import SwiftUI
import shared

extension Navigation {
        
    @ViewBuilder func screenPicker(screenState: ObservableScreenState) -> some View {
        
        VStack {
            switch screenState.requestedSId.screen {
                
            case .countrieslist:
                CountriesListScreen(
                    observableScreenState: screenState,
                    onListItemClick: { name in self.navigate(.countrydetail, CountryDetailParams(countryName: name)) },
                    onFavoriteIconClick: { name in self.stateManager.events.selectFavorite(countryName: name) }
                )
                
            case .countrydetail:
                CountryDetailScreen(
                    observableScreenState: screenState
                )
                
            default:
                EmptyView()
            }
            
        }
        .navigationTitle(self.stateManager.currentScreenIdentifier(screenStack: ScreenStack.main).getScreenInitSettings(stateManager: self.stateManager).title)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            if screenState.requestedSId.URI == (self.screenStackToNavigationState[ScreenStack.main]! as! NavigationState).topScreenIdentifier.URI {
                NSLog("iOS side:  onAppear URI "+screenState.requestedSId.URI)
            }
        }
        .onDisappear {
            self.exitScreenForIos(screenStack: ScreenStack.main, screenIdentifier: screenState.requestedSId)
        }
        .task {
            await screenState.collectScreenStateFlow()
        }
        
    }
    
    
    
    
    @ViewBuilder func twoPaneDefaultDetail(level1ScreenIdentifier: ScreenIdentifier) -> some View {
        
        switch level1ScreenIdentifier.screen {
            
        case .countrieslist: CountriesListTwoPaneDefaultDetail()
            
        default:
            EmptyView()
        }
        
    }
    
    
}

