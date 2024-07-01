package com.wonddak.print

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
actual fun WebView(modifier: Modifier) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            WebView(context).apply {
                with(settings) {
                    javaScriptEnabled = true
                }
                addJavascriptInterface(JSPrint(this), "printM")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.evaluateJavascript(
                            "window.print=()=>{printM.printRequest(window.document.title);}",
                            null
                        )
                    }
                }
            }
        },
        modifier = Modifier.then(modifier),
        update = { it.loadUrl(BundleAssetHelper.changeToLocalAddress("test.html")) }
    )
}

class JSPrint(
    private val webView: WebView
) {
    @JavascriptInterface
    fun printRequest(title:String) {
        webView.post {
            val printManager = webView.context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter: PrintDocumentAdapter = webView.createPrintDocumentAdapter(title)
            printManager.print(
                title,
                printAdapter,
                PrintAttributes.Builder().build()
            )
        }
    }
}