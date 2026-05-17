package com.example.myapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.favre.lib.crypto.bcrypt.BCrypt
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
    }

    fun savePassword(context: Context, password: String) {
        val hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val prefs = getSecurePrefs(context)
        prefs.edit().putString("password_hash", hashed).apply()
    }

    fun checkPassword(context: Context, password: String): Boolean {
        val prefs = getSecurePrefs(context)
        val hashedPassword = prefs.getString("password_hash", null)
        
        return if (hashedPassword != null) {
            BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
        } else {
            false
        }
    }

    private fun getSecurePrefs(context: Context): android.content.SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
