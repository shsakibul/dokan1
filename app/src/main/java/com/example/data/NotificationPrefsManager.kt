package com.example.data

import android.content.Context

object NotificationPrefsManager {
    private const val PREFS_NAME = "shohoj_hisab_notifications"
    private const val KEY_LOW_STOCK = "low_stock_alerts"
    private const val KEY_CUSTOMER_DUE = "customer_due_alerts"
    private const val KEY_GENERAL_ALERTS = "general_alerts"

    fun isLowStockEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOW_STOCK, true)
    }

    fun setLowStockEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LOW_STOCK, enabled).apply()
    }

    fun isCustomerDueEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_CUSTOMER_DUE, true)
    }

    fun setCustomerDueEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_CUSTOMER_DUE, enabled).apply()
    }

    fun isGeneralEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GENERAL_ALERTS, true)
    }

    fun setGeneralEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GENERAL_ALERTS, enabled).apply()
    }
}
