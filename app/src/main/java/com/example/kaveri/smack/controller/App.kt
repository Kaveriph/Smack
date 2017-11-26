package com.example.kaveri.smack.controller

import android.app.Application
import com.example.kaveri.smack.utilities.SharedPrefs

/**
 * Created by KAVERI on 11/26/2017.
 */
class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }



}