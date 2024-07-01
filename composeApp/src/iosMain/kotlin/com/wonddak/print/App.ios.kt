package com.wonddak.print

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebView(modifier: Modifier) {

    val webView = remember {
        WKWebView().apply {
            configuration.defaultWebpagePreferences.allowsContentJavaScript = true
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

}