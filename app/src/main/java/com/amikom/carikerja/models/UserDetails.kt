package com.amikom.carikerja.models

data class UserDetails(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var role: String? = null,
    var phone: String? = null,
    var password: String? = null,
    var bod: String? = null,
    var address: String? = null
)