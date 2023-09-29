package com.takwolf.android.demo.jsbridge

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.takwolf.android.demo.jsbridge.databinding.ActivityMainBinding
import com.takwolf.android.jsbridge.registerJsService
import com.takwolf.android.jsbridge.setupJsBridge

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        WebView.setWebContentsDebuggingEnabled(true)

        binding.web.settings.javaScriptEnabled = true
        binding.web.setupJsBridge()

        binding.web.registerJsService("service_1") { params, executor ->
            Log.i(TAG, "call 'service_1': $params")
            executor.resolve("ok")
        }

        binding.web.registerJsService("service_2") { params, executor ->
            Log.i(TAG, "call 'service_2': $params")
            executor.reject("error")
        }

        binding.web.loadUrl("file:///android_asset/www/index.html")

        setContentView(binding.root)
    }
}
