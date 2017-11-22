package com.example.kaveri.smack.controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kaveri.smack.AsyncTask.LoginAsyncTask
import com.example.kaveri.smack.R
import com.example.kaveri.smack.model.RegisterUser
import com.example.kaveri.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginClicked(view: View) {

        AuthService.loginUser(this, RegisterUser(emailText.text.toString(),passwordText.text.toString()), {succ ->
                    Toast.makeText(this, "success : $succ", Toast.LENGTH_SHORT).show()
            if(succ) {
                println("Token : ${AuthService.token}")
            }
        })
    }

    fun signUpHereClicked(view:View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

}
