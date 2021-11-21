package com.example.annunciator.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.annunciator.Crime
import com.example.annunciator.CrimeRepository
import java.io.File
import java.util.*

class CrimeDetailViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeID: UUID) {
        crimeIdLiveData.value = crimeID
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}