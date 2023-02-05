package com.amikom.carikerja.utils

import android.content.Context
import android.provider.Contacts.Intents.UI
import com.amikom.carikerja.R
import com.amikom.carikerja.utils.SharedPrefManager.getEncryptedSpf
import com.amikom.carikerja.utils.SharedPrefManager.getNoEncryptedSpf


object SharedPreferences {

    const val UID = ""

    fun saveUid(context: Context, uid: String){
        saveUid(context, UID, uid)
    }

    fun getUid(context: Context): String? {
        return getStringUid(context, UID)
    }

    fun getStringUid(context: Context, key: String): String? {
        val prefs = getEncryptedSpf(context)
        return prefs?.getString(this.UID, null)
    }

    fun saveUid(context: Context, key: String, value: String){
        var prefs = getEncryptedSpf(context)
        var editor = prefs?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun clearData(context: Context){
        var prefs = getEncryptedSpf(context)
        var editor = prefs?.edit()
        editor?.clear()
        editor?.apply()
    }
}