package com.amikom.carikerja.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.Nullable
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amikom.carikerja.R

object SharedPrefManager {

    fun getNoEncryptedSpf(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    @Nullable
    fun getEncryptedSpf(context: Context?): SharedPreferences? {
        return try {
            // Tentukan Spek Keygen nya terlebih dahulu
            val spec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            } else {
                TODO("VERSION.SDK_INT < M")
            }

            // Buat Masterkey berdasarkan spek keygen
            val masterKey: MasterKey = MasterKey.Builder(context!!)
                .setKeyGenParameterSpec(spec)
                .build()

            // lalu buat EncryptedSharedPreferences nya
            EncryptedSharedPreferences.create(
                context,
                context.getString(R.string.app_name),
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}