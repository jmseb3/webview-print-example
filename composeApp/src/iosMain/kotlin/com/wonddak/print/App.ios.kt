package com.wonddak.print

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.UIKit.UIPrintInfo
import platform.UIKit.UIPrintInfoOutputType
import platform.UIKit.UIPrintInteractionController
import platform.UIKit.viewPrintFormatter
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebView(modifier: Modifier) {

    val webView = remember {
        WKWebView().apply {
            configuration.defaultWebpagePreferences.allowsContentJavaScript = true
            configuration.userContentController.addScriptMessageHandler(
                PrintDelegate(this),
                "printM"
            )
            navigationDelegate = WebDelegate()
        }
    }

    val localFilePath = BundleAssetHelper.changeToLocalAddress("test.html")
    val request = NSMutableURLRequest.requestWithURL(URL = NSURL(fileURLWithPath = localFilePath))

    webView.apply {
        loadRequest(request)
        allowsBackForwardNavigationGestures = true // Swipe Back 제스쳐
    }

    UIKitView(
        factory = { webView },
        modifier = Modifier.fillMaxSize().then(modifier)
    )
}

class WebDelegate() : NSObject(), WKNavigationDelegateProtocol {
    @ObjCSignatureOverride
    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        webView.evaluateJavaScript(
            "window.print=()=>{window.webkit.messageHandlers.printM.postMessage(document.title);}",
            null
        )
    }

}

class PrintDelegate(
    private val webView: WKWebView,
) : NSObject(), WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        if (didReceiveScriptMessage.name == "printM") {
            runCatching {
                didReceiveScriptMessage.body() as String
            }.onSuccess { printName ->
                val printController = webView.viewPrintFormatter()

                val printInfo = UIPrintInfo.printInfoWithDictionary(null)
                printInfo.outputType = UIPrintInfoOutputType.UIPrintInfoOutputGeneral
                printInfo.jobName = printName

                val printInteractionController =
                    UIPrintInteractionController.sharedPrintController()
                printInteractionController.printInfo = printInfo
                printInteractionController.printFormatter = printController
                printInteractionController.presentAnimated(true) { controller, completed, error ->
                    if (completed) {
                        print("Printing completed")
                    } else {
                        error?.let { err ->
                            print("Printing failed with error: ${err.localizedDescription}")
                        } ?: print("Printing was canceled")
                    }
                }
            }
        }
    }
}