package com.takwolf.android.jsbridge

import android.webkit.WebView

fun WebView.setupJsBridge() {
    JsBridge.setup(this)
}

fun WebView.registerBridgeService(name: String, handler: ServiceHandler) {
    JsBridge.registerService(this, name, handler)
}

fun WebView.unregisterBridgeService(name: String) {
    JsBridge.unregisterService(this, name)
}

fun WebView.setBridgeErrorHandler(handler: ErrorHandler?) {
    JsBridge.setErrorHandler(this, handler)
}
