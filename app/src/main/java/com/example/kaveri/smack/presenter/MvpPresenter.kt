package com.example.kaveri.smack.presenter

import android.content.Context
import com.android.volley.Response
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import io.socket.client.Socket
import org.json.JSONArray

/**
 * Created by KAVERI on 12/16/2017.
 */
interface MvpPresenter {
    fun findUserByEmail(context:Context)
    fun createSocket()
    fun getSocket(): Socket
    fun getChannels()
    fun addMessage(newMessage: Message)
    fun addNewChannel(newChannel: Channel)
    fun getMessages(selectedChannel: Channel)
    fun getUserName(): String?
    fun getUserEmail(): String?
    fun getUserAvatar(): String
    fun getAvatarColor(): String
    fun getAvatarColorInRgb(): Int
    fun getUserId(): String
    fun logOut()
    fun getStoredChannels(): ArrayList<Channel>
    fun getStoredMessages(): ArrayList<Message>
    fun clearMessages()
    fun clearChannels()

    //implemented by data manager -
    fun foundMessages(response: JSONArray, complete: (Boolean) -> Unit)

    fun foundChannels(response: JSONArray, complete: (Boolean) -> Unit)
}