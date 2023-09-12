import eu.baroncelli.dkmpsample.composables.MainComposable
import eu.baroncelli.dkmpsample.composables.styling.MyMaterialTheme
import eu.baroncelli.dkmpsample.shared.viewmodel.DKMPViewModel
import eu.baroncelli.dkmpsample.shared.viewmodel.getWebInstance
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.HTMLCanvasElement


fun main() = application { model ->
    onWasmReady {
        maxResizeCanvase()
        BrowserViewportWindow("D-KMP Sample") {
            MyMaterialTheme {
                MainComposable(model)
            }
        }
    }
}

fun maxResizeCanvase() {
    val canvas: HTMLCanvasElement = document.getElementById("ComposeTarget") as HTMLCanvasElement
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
}

// this coroutine mechanism is required, because SqlDelight for Web
// (which is part of the DKMPViewModel) needs to be instantiated asynchronously
fun application(block: suspend (DKMPViewModel) -> Unit) {
    MainScope().launch {
        val model = DKMPViewModel.getWebInstance()
        block(model)
    }
}