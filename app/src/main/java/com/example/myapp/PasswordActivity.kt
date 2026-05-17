package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.favre.lib.crypto.bcrypt.BCrypt
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        if (!passwordExists()) {
            savePassword("1234")
        }

        loginBtn.setOnClickListener {
            val password = passwordInput.text.toString()
            if (checkPassword(password)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "كلمة السر غلط", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun passwordExists(): Boolean {
        val prefs = getSecurePrefs()
        return prefs.contains("password_hash")
    }

    private fun savePassword(password: String) {
        val hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        getSecurePrefs().edit().putString("password_hash", hashed).apply()
    }

    private fun checkPassword(password: String): Boolean {
        val hashedPassword = getSecurePrefs().getString("password_hash", null)
        return hashedPassword != null && 
               BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }

    private fun getSecurePrefs(): android.content.SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
