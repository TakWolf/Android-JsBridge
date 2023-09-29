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

    private static final ErrorHandler defaultErrorHandler = new ErrorHandler() {
        @Override
        public void onNoService(@NonNull String name, @Nullable String params, @NonNull PromiseExecutor executor) {
            Log.e(TAG, "No service handler: " + name);
            executor.reject(null);
        }

        @Override
        public void onException(@NonNull String name, @Nullable String params, @NonNull Throwable e, @NonNull PromiseExecutor executor) {
            Log.e(TAG, "Service call exception: " + name, e);
            executor.reject(null);
        }
    };

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
        jsBridge.serviceHandlers.put(name, handler);
    }

    public static void unregisterService(@NonNull WebView webView, @NonNull String name) {
        JsBridge jsBridge = with(webView);
        jsBridge.serviceHandlers.remove(name);
    }

    public static void setErrorHandler(@NonNull WebView webView, @Nullable ErrorHandler handler) {
        JsBridge jsBridge = with(webView);
        if (handler == null) {
            jsBridge.errorHandler = defaultErrorHandler;
        } else {
            jsBridge.errorHandler = handler;
        }
    }

    private final WebView webView;
    private final Map<String, ServiceHandler> serviceHandlers = new HashMap<>();
    @NonNull private ErrorHandler errorHandler = defaultErrorHandler;

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
    public void callService(long callId, @Nullable String name, @Nullable String params) {
        if (name == null || "undefined".equals(name)) {
            name = "";
        }
        if ("undefined".equals(params)) {
            params = null;
        }
        ServiceHandler handler = serviceHandlers.get(name);
        PromiseExecutor executor = new PromiseExecutor() {
            @Override
            public void resolve(@Nullable String result) {
                doCallback(callId, "resolve", result);
            }

            @Override
            public void reject(@Nullable String error) {
                doCallback(callId, "reject", error);
            }
        };
        if (handler == null) {
            errorHandler.onNoService(name, params, executor);
        } else {
            try {
                handler.onCall(params, executor);
            } catch (Exception e) {
                errorHandler.onException(name, params, e, executor);
            }
        }
    }
}
