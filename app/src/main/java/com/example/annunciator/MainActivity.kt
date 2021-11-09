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

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, CrimeListFragment.newInstance())
                .commit()
        }

    }
}
