package com.amikom.carikerja.models

data class Message(

    var registration_ids: Array<String>,
    var data: Notification

)

data class Notification(

    val title: String? = null,
    val body: String? = null

)