import SwiftUI
import shared

class ObservableScreenState: ObservableObject {
    @Published var state: (any ScreenState)

    var screenStack: ScreenStack
    var requestedSId: ScreenIdentifier
    private var stateProvider: StateProvider
    
    init(screenStack: ScreenStack, requestedSId: ScreenIdentifier, stateProvider: StateProvider, state: (any ScreenState)? = nil) {
        self.screenStack = screenStack
        self.requestedSId = requestedSId
        self.stateProvider = stateProvider
        self.state = state ?? stateProvider.getToCast(screenStack: screenStack, screenIdentifier: requestedSId).value
    }
    
    
    @MainActor
    func collectScreenStateFlow() async {
        for await state in stateProvider.getToCast(screenStack: screenStack, screenIdentifier: requestedSId) {
            self.state = state
        }
    }
}
