package com.example.annunciator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.annunciator.databinding.ActivityMainBinding
import com.example.annunciator.fragments.CrimeListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        TODO ("Упражнение глава 12. Эффективный RecyclerView")

    }
}

// TODO (Упражнение глава 9. Разные типы преступления)

