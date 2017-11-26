package com.example.kaveri.smack.utilities

import android.content.Context
import com.android.volley.toolbox.Volley

/**
 * Created by KAVERI on 11/26/2017.
 */
class SharedPrefs(context : Context) {
    val PREFS_FILENAME = "Prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    val IS_LOGGED_IN ="is_logged_in"
    val AUTH_TOKEN = "auth_token"
    val USER_EMAIL = "user_email"
    val requestQueue = Volley.newRequestQueue(context)

    var isLoggedIn: Boolean
            get() = prefs.getBoolean(IS_LOGGED_IN, false)
            set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
            get() = prefs.getString(AUTH_TOKEN,"")
            set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
            get() = prefs.getString(USER_EMAIL,"")
            set(value) = prefs.edit().putString(USER_EMAIL, value).apply()
}