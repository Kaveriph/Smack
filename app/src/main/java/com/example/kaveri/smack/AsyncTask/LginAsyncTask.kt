package com.example.kaveri.smack.AsyncTask

import android.content.Context
import android.os.AsyncTask
import com.example.kaveri.smack.model.RegisterUser
import com.example.kaveri.smack.services.AuthService
import okhttp3.Response

/**
 * Created by KAVERI on 11/18/2017.
 */
class LoginAsyncTask(val context : Context) : AsyncTask<String, Void, Response>() {
    val TAG = "LoginAsyncTask"
    override fun doInBackground(vararg p0: String?): Response {
        return AuthService.loginUser( RegisterUser("dummyUser","dummyPasword"))
    }

    override fun onPostExecute(result: Response?) {
        super.onPostExecute(result)
        if(result != null)
            println("$TAG $result")
    }
}