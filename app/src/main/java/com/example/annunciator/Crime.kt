package com.example.annunciator

import java.text.SimpleDateFormat
import java.util.*

data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: SimpleDateFormat = SimpleDateFormat("dd.M.yyyy hh:mm:ss"),
    var isSolved: Boolean = false,
    var requiresPolicy: Boolean = false
)