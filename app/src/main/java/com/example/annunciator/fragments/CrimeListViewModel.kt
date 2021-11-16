package com.example.annunciator.fragments

import androidx.lifecycle.ViewModel
import com.example.annunciator.Crime
import com.example.annunciator.CrimeRepository

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }

    fun deleteCrime(crime: Crime) {
        crimeRepository.deleteCrime(crime)
    }
}