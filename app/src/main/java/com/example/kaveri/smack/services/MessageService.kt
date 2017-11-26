package com.example.kaveri.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.kaveri.smack.controller.App
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.utilities.AUTH_KEY_NAME
import com.example.kaveri.smack.utilities.URL_GET_CHANNELS
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by KAVERI on 11/26/2017.
 */
object MessageService {
    var channels = ArrayList<Channel>()
    val MEDIA_TYPE = "application/json; charset=utf-8"
    val TAG = "MessageService"


    fun getChannels(complete:(Boolean) -> Unit) {
        var channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            Log.d(TAG, "response code  ${response}")
            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val id = channel.getString("_id")
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val newChannel = Channel(name, desc, id)
                    channels.add(newChannel)
                    complete(true)
                }

            } catch (e: JSONException) {
                Log.d(TAG, "Json exception ${e.printStackTrace()}")
            }

        }, Response.ErrorListener { error ->
            error.printStackTrace()
        }) {
            override fun getBodyContentType(): String {
                return MEDIA_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AUTH_KEY_NAME,"Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(channelRequest)
    }
}