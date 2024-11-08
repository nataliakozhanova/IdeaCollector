package com.example.ideacollector.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}
