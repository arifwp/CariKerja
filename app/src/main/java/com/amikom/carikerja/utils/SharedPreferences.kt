package com.amikom.carikerja.utils

import android.content.Context
import android.provider.Contacts.Intents.UI
import com.amikom.carikerja.R
import com.amikom.carikerja.utils.SharedPrefManager.getEncryptedSpf
import com.amikom.carikerja.utils.SharedPrefManager.getNoEncryptedSpf


object SharedPreferences {

    const val UID = "uid"
    const val role = "role"

    fun saveUid(context: Context, uid: String){
        saveUid(context, UID, uid)
    }

    fun saveRole(context: Context, roleData: String){
        saveStringRole(context, role, roleData)
    }

    fun getUid(context: Context): String? {
        return getStringUid(context, UID)
    }

    fun getRole(context: Context): String? {
        return getStringRole(context, role)
    }

    fun getStringUid(context: Context, key: String): String? {
        val prefs = getEncryptedSpf(context)
        return prefs?.getString(this.UID, null)
    }

    fun getStringRole(context: Context, keyRole: String): String? {
        val prefsRole = getEncryptedSpf(context)
        return prefsRole?.getString(this.role, null)
    }

    fun saveUid(context: Context, key: String, value: String){
        var prefs = getEncryptedSpf(context)
        var editor = prefs?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun saveStringRole(context: Context, keyRole:String, valueRole: String){
        var prefsRole = getEncryptedSpf(context)
        var editorRole = prefsRole?.edit()
        editorRole?.putString(keyRole, valueRole)
        editorRole?.apply()
    }

    fun clearData(context: Context){
        var prefsRole = getEncryptedSpf(context)
        var editorRole = prefsRole?.edit()
        editorRole?.clear()
        editorRole?.apply()
    }
}