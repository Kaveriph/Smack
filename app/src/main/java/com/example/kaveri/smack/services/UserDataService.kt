package com.example.kaveri.smack.services

import android.graphics.Color
import com.example.kaveri.smack.controller.App
import com.example.kaveri.smack.presenter.MainMvpPresenter
import com.example.kaveri.smack.presenter.MvpPresenter
import java.util.*

/**
 * Created by KAVERI on 11/19/2017.
 */
object UserDataService {
    var name:String=""
    var email = ""
    var avatarName = ""
    var avatarColor = ""
    var id= ""

    fun logout (mMainMvpPresenter: MvpPresenter) {
        id = ""
        name = ""
        email = ""
        avatarName = ""
        avatarColor = ""
        App.prefs.authToken = ""
        App.prefs.isLoggedIn = false
        App.prefs.userEmail= ""
        mMainMvpPresenter.clearChannels()
        mMainMvpPresenter.clearMessages()
    }

    fun returnAvatarColorInRgb(componets:String):Int  {

        var stripedColor = componets
        stripedColor = stripedColor.replace("[","")
        .replace("]","")
        .replace(",","")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(stripedColor)
        if(scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return  Color.rgb(r,g,b)
    }

}