package com.wonddak.print

actual object BundleAssetHelper {
    actual fun changeToLocalAddress(fileName: String): String {
        return "file:///android_asset/$fileName"
    }
}