package com.example.kaveri.smack.services

import android.graphics.Color
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

    fun logout () {
        id = ""
        name = ""
        email = ""
        avatarName = ""
        avatarColor = ""
        AuthService.token = ""
        AuthService.isLogedIn = false
        //AuthService.userEmail = ""
    }
    fun returnAvatarColor(componets:String):Int  {

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