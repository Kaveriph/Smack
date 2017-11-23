package com.example.kaveri.smack.controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        enableDisableSpinner(true);
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(this, RegisterUser(email, password), { succ ->
                Toast.makeText(this, "success : $succ", Toast.LENGTH_SHORT).show()
                if (succ) {
                    println("Token : ${AuthService.token}")
                    AuthService.findUserByEmail(this, { findSucc ->
                        enableDisableSpinner(false);
                        if (findSucc) {
                            println("found the user sucessfully")
                            finish()
                        } else {
                            println("failed to find the user")
                        }
                    })
                } else {
                    displayError("Failed to login");
                    enableDisableSpinner(false);
                }
            })
        } else {
            displayError("")
            enableDisableSpinner(false)
        }
    }

    private fun enableDisableSpinner(enable:Boolean) {
        if(enable)
            progressBar.visibility = View.VISIBLE;
        else
            progressBar.visibility = View.INVISIBLE;

        login_loginBtn.isEnabled = !enable;
        signUpText.isEnabled = !enable;
        no_account_text.isEnabled = !enable;
    }

    private fun displayError(strMsg: String) {
        Toast.makeText(this,strMsg, Toast.LENGTH_SHORT);
    }

    fun signUpHereClicked(view:View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

}
