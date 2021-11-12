package com.example.annunciator.fragments

import androidx.lifecycle.ViewModel
import com.example.annunciator.CrimeRepository

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()
}