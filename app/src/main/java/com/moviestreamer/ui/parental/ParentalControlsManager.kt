package com.moviestreamer.ui.parental

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages parental control PIN using EncryptedSharedPreferences (Jetpack Security).
 *
 * The PIN is stored as a SHA-256 hash — the plaintext PIN is never persisted.
 * This class does NOT depend on any Android Security Keystore knowledge from the
 * caller; all keystore operations are handled internally.
 */
class ParentalControlsManager(private val context: Context) {

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    val isPinSet: Boolean
        get() = prefs.contains(KEY_PIN_HASH)

    fun setPin(pin: String) {
        require(pin.length >= MIN_PIN_LENGTH) { "PIN must be at least $MIN_PIN_LENGTH digits" }
        prefs.edit { putString(KEY_PIN_HASH, pin.sha256()) }
    }

    fun verifyPin(pin: String): Boolean = prefs.getString(KEY_PIN_HASH, null) == pin.sha256()

    fun clearPin() {
        prefs.edit { remove(KEY_PIN_HASH) }
    }

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_ENABLED, value) }

    private fun String.sha256(): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_FILE = "parental_controls"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_ENABLED = "enabled"
        const val MIN_PIN_LENGTH = 4
    }
}
