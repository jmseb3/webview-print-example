package com.wonddak.print

import android.app.Application
import android.os.Bundle
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
                webViewClient = object : WebViewClient() {

                }
            }
        },
        modifier = Modifier.then(modifier),
        update = { it.loadUrl(BundleAssetHelper.changeToLocalAddress("test.html")) }
    )
}