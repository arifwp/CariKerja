package com.amikom.carikerja.utils

sealed class AllEvents {
    data class Message(val message : String) : AllEvents()
    data class Code(val code : Int):AllEvents()
    data class ErrorMessage(val error : String) : AllEvents()
}