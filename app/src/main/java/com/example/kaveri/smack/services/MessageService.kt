package com.example.kaveri.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.kaveri.smack.controller.App
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.utilities.AUTH_KEY_NAME
import com.example.kaveri.smack.utilities.URL_GET_CHANNELS
import com.example.kaveri.smack.utilities.URL_GET_MESSAGES_BY_CHANNEL
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
    val messages = ArrayList<Message>()

    fun getChannels(complete:(Boolean) -> Unit) {
        var channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            Log.d(TAG, "response code  ${response}")
            try {
                channels.clear()
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val id = channel.getString("_id")
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val newChannel = Channel(name, desc, id)
                    channels.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d(TAG, "Json exception ${e.printStackTrace()}")
                complete(false)
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

    fun  getMessages(channelId:String, complete:(Boolean) -> Unit) {
        val getUrl = URL_GET_MESSAGES_BY_CHANNEL+channelId
        var messageRequest = object : JsonArrayRequest(Method.GET, getUrl, null, Response.Listener { response ->
            try {
                messages.clear()
                for(x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userId = message.getString("userId")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")
                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    MessageService.messages.add(newMessage)
                }
                complete(true)
            } catch (e:JSONException){
                Log.e(TAG,"Exception : ${e.printStackTrace()}")
                complete(false)
            }

        }, Response.ErrorListener { error ->

            Log.e(TAG,"Failed to get the messages")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return MEDIA_TYPE
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AUTH_KEY_NAME, "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}