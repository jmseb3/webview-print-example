package com.wonddak.print

import platform.Foundation.NSBundle

actual object BundleAssetHelper {
    actual fun changeToLocalAddress(fileName: String): String {
        val (name,prefix) = fileName.split(".")
        return NSBundle.mainBundle().pathForResource(name = name, ofType =  prefix, inDirectory = "www")!!
    }
}