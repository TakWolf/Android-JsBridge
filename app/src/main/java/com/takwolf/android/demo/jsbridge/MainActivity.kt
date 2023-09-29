package com.takwolf.android.demo.jsbridge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takwolf.android.demo.jsbridge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}
