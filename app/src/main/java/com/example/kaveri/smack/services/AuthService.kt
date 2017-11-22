package com.example.kaveri.smack.services

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.kaveri.smack.R
import com.example.kaveri.smack.model.CreateUser
import com.example.kaveri.smack.model.RegisterUser
import com.example.kaveri.smack.utilities.AUTH_KEY_NAME
import com.example.kaveri.smack.utilities.URL_CREATE_USER
import com.example.kaveri.smack.utilities.URL_LOGIN
import com.example.kaveri.smack.utilities.URL_REGISTER
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by KAVERI on 11/18/2017.
 */
object AuthService {

    val TAG = AuthService.javaClass.simpleName
    val MEDIA_TYPE = "application/json; charset=utf-8"
    lateinit var token: String
    lateinit var email: String
    var isLogedIn: Boolean = false

    fun registerUser(context : Context, user:RegisterUser, complete: (Boolean) -> Unit) {

         var jsonObg = Gson().toJson(user)

         var request = object : StringRequest(Method.POST, URL_REGISTER, com.android.volley.Response.Listener { response ->
             println("$TAG response")
             complete(true)
         }, com.android.volley.Response.ErrorListener{ error: VolleyError ->
             println("$TAG Error: ${error.printStackTrace()}")
             complete(false)
         }) {
             override fun getBodyContentType(): String {
                 return MEDIA_TYPE
             }

             override fun getBody(): ByteArray {
                 return jsonObg.toByteArray()
             }

         }
         request.setRetryPolicy(DefaultRetryPolicy(30000,
                 DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
         Volley.newRequestQueue(context).add(request)
     }

    fun registerUser(context: Context, user: RegisterUser): Response {

        var requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), Gson().toJson(user).toString())
        var client = OkHttpClient()
        var request = Request.Builder()
                .url(URL_REGISTER)
                .post(requestBody)
                .build()

        var response = client.newCall(request).execute()
        return response
    }

    fun loginUser(context: Context, user: RegisterUser): Response {
        var jsonUser = Gson().toJson(user)
        var requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), jsonUser)
        var client = OkHttpClient()
        var request = Request.Builder()
                .url(URL_LOGIN)
                .post(requestBody)
                .build()
        val response = client.newCall(request).execute()
        return response
    }

    fun loginUser(context: Context, user: RegisterUser, complete: (Boolean) -> Unit) {
        var jsonUser = Gson().toJson(user)

        var request = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                com.android.volley.Response.Listener { response ->
                    println("$TAG response $response")
                    try {
                        email = response.getString("user")
                        token = response.getString("token")
                        isLogedIn = true
                    } catch (e: JSONException) {
                        println("Exception : ${e.printStackTrace()}")
                    }
                    complete(true)
                },
                com.android.volley.Response.ErrorListener { error ->
                    println("$TAG error $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return MEDIA_TYPE
            }

            override fun getBody(): ByteArray {
                return jsonUser.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    fun createUser(context:Context, createUser: CreateUser, complete:(Boolean) -> Unit) {
        val jsonObj = Gson().toJson(createUser)

        val request = object: JsonObjectRequest(Method.POST, URL_CREATE_USER, null, com.android.volley.Response.Listener{response ->
            println("response : $response")
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.id = response.getString("id")
                val name = response.getString("name")
            } catch(e:JSONException) {
                println("Exception caught while parsing : ${e.printStackTrace()}")
                complete(false)
            }
            complete(true)
        }, com.android.volley.Response.ErrorListener {
            error ->
            println("error $error")
            complete(false)
        }){
            override fun getBody(): ByteArray {
                return jsonObj.toByteArray()
            }

            override fun getBodyContentType(): String {
                return MEDIA_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AUTH_KEY_NAME,"Bearer ${AuthService.token}")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

}