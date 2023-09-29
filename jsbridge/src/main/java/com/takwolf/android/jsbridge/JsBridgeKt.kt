package com.takwolf.android.jsbridge

import android.webkit.WebView

fun WebView.setupJsBridge() {
    JsBridge.setup(this)
}

fun WebView.registerJsService(name: String, handler: ServiceHandler) {
    JsBridge.registerService(this, name, handler)
}

fun WebView.unregisterJsService(name: String) {
    JsBridge.unregisterService(this, name)
}

fun WebView.setDefaultJsServiceHandler(handler: ServiceHandler?) {
    JsBridge.setDefaultServiceHandler(this, handler)
}
