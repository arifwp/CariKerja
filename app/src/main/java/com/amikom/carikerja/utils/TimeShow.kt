package com.amikom.carikerja.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TimeShow {
    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "yang lalu"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second detik $suffix"
            } else if (minute < 60) {
                convTime = "$minute menit $suffix"
            } else if (hour < 24) {
                convTime = "$hour jam $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " tahun " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " bulan " + suffix
                } else {
                    (day / 7).toString() + " minggu " + suffix
                }
            } else if (day < 7) {
                convTime = "$day hari $suffix"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }
}