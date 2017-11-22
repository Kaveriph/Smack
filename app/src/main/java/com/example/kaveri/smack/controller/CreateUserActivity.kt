package com.example.kaveri.smack.controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.kaveri.smack.R
import com.example.kaveri.smack.model.CreateUser
import com.example.kaveri.smack.model.RegisterUser
import com.example.kaveri.smack.services.AuthService
import com.example.kaveri.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    val TAG = "CreateUserActivity"
    private var userAvatar= "profileDefault"
    private var avatarColor = "[0.5, 0.5, 0.5, 1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateBgColor(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        signupUserAvatarImg.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = (r.toDouble()/255)
        val savedG = (g.toDouble()/255)
        val savedB = (b.toDouble()/255)

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun onTapToGenerateAvatar(view:View) {

        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = when(color) {
            0 -> "light$avatar"
            else -> "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar,"drawable", this.packageName)
        signupUserAvatarImg.setImageResource(resourceId)
    }

    fun createUserClicked(view:View) {
        enableSpinner(true)
        val userName = signupUserNameText.text.toString()
        var registerUser = RegisterUser(signupEmailText.text.toString(), signupPasswordText.text.toString())
        if(userName.isNotEmpty() && signupEmailText.text.toString().isNotEmpty() && signupPasswordText.text.toString().isNotEmpty()) {
            AuthService.registerUser(this, registerUser, { registrationSucc ->
                if (registrationSucc) {
                    println("$TAG Registered the user succesfully")
                    AuthService.loginUser(this, registerUser, { loginSucc ->
                        if (loginSucc) {
                            println("$TAG Logged in successfully")
                            AuthService.createUser(this, CreateUser(userName, registerUser.email, userAvatar, avatarColor), { createUserSucc ->
                                if (createUserSucc) {
                                    println("$TAG created user succesfully")
                                    enableSpinner(false)
                                    var broadCastIntent = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent)
                                    finish()
                                } else {
                                    displayError("Failed to create user")
                                }
                            })
                        } else {
                            displayError("Failed to login ")
                        }
                    })
                } else {
                    displayError("Failed to register")
                }
            })
        } else {
            displayError("Username, email or password should not be empty")
        }
    }

    fun enableSpinner(enableDisable:Boolean) {
        if(enableDisable) {
            spinner.visibility = View.VISIBLE
        } else {
            spinner.visibility = View.INVISIBLE
        }
        generateBgBtn.isEnabled = !enableDisable
        generate_avatar.isEnabled = !enableDisable
        createUserBtn.isEnabled = !enableDisable
    }

    val displayError :  (String) -> Unit = {
        strMsg -> println("$TAG $strMsg")
        Toast.makeText(this,"$strMsg",Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }
}
