package com.takwolf.android.jsbridge;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class JsBridge {
    private static final String TAG = "JsBridge";

    private static final String NAME_NATIVE_BRIDGE = "_androidNativeBridge";
    private static final String NAME_CALLBACK_BRIDGE = "_androidCallbackBridge";

    @SuppressLint("JavascriptInterface")
    public static void setup(@NonNull WebView webView) {
        if (webView.getTag(R.id.jsbridge) != null) {
            return;
        }
        JsBridge jsBridge = new JsBridge(webView);
        webView.addJavascriptInterface(jsBridge, NAME_NATIVE_BRIDGE);
        webView.setTag(R.id.jsbridge, jsBridge);
    }

    @NonNull
    private static JsBridge with(@NonNull WebView webView) {
        JsBridge jsBridge = (JsBridge) webView.getTag(R.id.jsbridge);
        if (jsBridge == null) {
            throw new RuntimeException("JsBridge not setup");
        }
        return jsBridge;
    }

    public static void registerService(@NonNull WebView webView, @NonNull String name, @NonNull ServiceHandler handler) {
        JsBridge jsBridge = with(webView);
        jsBridge.handlerRegistry.put(name, handler);
    }

    public static void unregisterService(@NonNull WebView webView, @NonNull String name) {
        JsBridge jsBridge = with(webView);
        jsBridge.handlerRegistry.remove(name);
    }

    public static void setDefaultServiceHandler(@NonNull WebView webView, @Nullable ServiceHandler handler) {
        JsBridge jsBridge = with(webView);
        jsBridge.defaultHandler = handler;
    }

    private final WebView webView;
    private final Map<String, ServiceHandler> handlerRegistry = new HashMap<>();
    @Nullable private ServiceHandler defaultHandler;

    private JsBridge(@NonNull WebView webView) {
        this.webView = webView;
    }

    private void doCallback(long callId, @NonNull String state, @Nullable String result) {
        String script;
        if (result == null) {
            script = String.format("window.%s.%s(%s);", NAME_CALLBACK_BRIDGE, state, callId);
        } else {
            script = String.format("window.%s.%s(%s, '%s');", NAME_CALLBACK_BRIDGE, state, callId, result.replace("'", "\\'"));
        }
        webView.post(() -> webView.evaluateJavascript(script, null));
    }

    @JavascriptInterface
    public void callService(long callId, @NonNull String name, @Nullable String params) {
        if ("undefined".equals(params)) {
            params = null;
        }
        ServiceHandler handler = handlerRegistry.getOrDefault(name, defaultHandler);
        if (handler == null) {
            Log.w(TAG, "No service handler: [callId: " + callId + "] " + name);
            doCallback(callId, "reject", null);
        } else {
            handler.onCall(params, new PromiseExecutor() {
                @Override
                public void resolve(@Nullable String result) {
                    doCallback(callId, "resolve", result);
                }

                @Override
                public void reject(@Nullable String error) {
                    doCallback(callId, "reject", error);
                }
            });
        }
    }
}
