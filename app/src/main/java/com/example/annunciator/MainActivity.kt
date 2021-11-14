package com.example.annunciator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.annunciator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}

// TODO (Упражнение глава 9. Разные типы преступления)
// TODO (Упражнение глава 13. TimePicker)
//

