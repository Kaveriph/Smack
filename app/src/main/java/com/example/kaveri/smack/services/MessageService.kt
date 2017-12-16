package com.example.kaveri.smack.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.kaveri.smack.controller.App
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.presenter.MainMvpPresenter
import com.example.kaveri.smack.presenter.MvpPresenter
import com.example.kaveri.smack.utilities.AUTH_KEY_NAME
import com.example.kaveri.smack.utilities.URL_GET_CHANNELS
import com.example.kaveri.smack.utilities.URL_GET_MESSAGES_BY_CHANNEL
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by KAVERI on 11/26/2017.
 */
class MessageService {
    var channels = ArrayList<Channel>()
    val MEDIA_TYPE = "application/json; charset=utf-8"
    val TAG = "MessageService"
    val messages = ArrayList<Message>()

    fun getChannels(complete:(Boolean) -> Unit, mMainMvpPresenter: MvpPresenter) {
        var channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            Log.d(TAG, "response code  ${response}")
            mMainMvpPresenter.foundChannels(response, complete)
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

    fun  getMessages(channelId:String, mMainMvpPresenter: MvpPresenter, complete:(Boolean) -> Unit) {
        val getUrl = URL_GET_MESSAGES_BY_CHANNEL+channelId
        var messageRequest = object : JsonArrayRequest(Method.GET, getUrl, null, Response.Listener { response ->
           mMainMvpPresenter.foundMessages(response, complete)
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