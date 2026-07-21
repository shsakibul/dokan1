package com.example.data

import android.content.Context

object SecurityManager {
    private const val PREFS_NAME = "shohoj_hisab_security"
    private const val KEY_PIN = "security_pin"
    private const val KEY_PIN_ENABLED = "pin_enabled"

    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_PIN, pin)
            .putBoolean(KEY_PIN_ENABLED, true)
            .apply()
    }

    fun getPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, null)
    }

    fun isPinEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PIN_ENABLED, false) && !getPin(context).isNullOrEmpty()
    }

    fun disablePin(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_PIN_ENABLED, false)
            .remove(KEY_PIN)
            .apply()
    }
}
