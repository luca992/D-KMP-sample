package eu.baroncelli.dkmpsample.shared.viewmodel

import co.touchlab.kermit.Logger
import eu.baroncelli.dkmpsample.shared.datalayer.Repository

val debugLogger by lazy {
    val logger = Logger
    logger.setTag("D-KMP SAMPLE")
    logger
}

class DKMPViewModel(repo: Repository) {

    companion object Factory {
        // factory methods are defined in the platform-specific shared code (androidMain and iosMain)
    }

    val stateManager by lazy { StateManager(repo) }

}