package com.example.ideacollector.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ideacollector.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private var _binding: ActivityRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}