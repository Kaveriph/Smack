package com.example.kaveri.smack.presenter

import android.content.Context
import android.util.Log
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.services.AuthService
import com.example.kaveri.smack.services.MessageService
import com.example.kaveri.smack.services.UserDataService
import com.example.kaveri.smack.utilities.SOCKET_URL
import com.example.kaveri.smack.views.interfaces.MainMvpView
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException

/**
 * Created by KAVERI on 12/16/2017.
 */
class MainMvpPresenter(val mMainMvpView: MainMvpView):MvpPresenter {

    val TAG = "MainMvpPresenter"

    override fun foundChannels(response: JSONArray, complete: (Boolean) -> Unit) {
        try {
            clearMessages()
            for (x in 0 until response?.length()) {
                val channel = response?.getJSONObject(x)
                val id = channel.getString("_id")
                val name = channel.getString("name")
                val desc = channel.getString("description")
                val newChannel = Channel(name, desc, id)
                addNewChannel(newChannel)
            }
            complete(true)
        } catch (e: JSONException) {
            Log.d(TAG, "Json exception ${e.printStackTrace()}")
            complete(false)
        }
    }

    val mMessageService:MessageService = MessageService()
    private lateinit var socket:Socket


    override fun createSocket() {
        socket = IO.socket(SOCKET_URL)
        socket.connect()
        socket.on("channelCreated", mMainMvpView.onNewChannel())
        socket.on("messageCreated", mMainMvpView.onNewMessage())
    }

    override fun getSocket():Socket {
        return socket
    }
    override fun findUserByEmail(context: Context) {
        AuthService.findUserByEmail(context){}
    }

    override fun getMessages(selectedChannel:Channel) {
        mMessageService.getMessages(selectedChannel!!.id, this) { success ->
           mMainMvpView.foundMessages(success)
        }
    }

    override fun getChannels() {
        mMessageService.getChannels({ foundChannels ->
            mMainMvpView.foundChannels(foundChannels, getStoredChannels())
        }, this)
    }

    override fun logOut() {
        UserDataService.logout(this)
    }

    //Model access

    override fun getUserName(): String? {
        return UserDataService.name
    }

    override fun getUserEmail(): String? {
        return UserDataService.email
    }


    override fun getUserId(): String {
        return UserDataService.id
    }

    override fun getAvatarColorInRgb(): Int {
        return UserDataService.returnAvatarColorInRgb(UserDataService.avatarColor)
    }

    override fun getAvatarColor(): String {
        return UserDataService.avatarColor
    }

    override fun getUserAvatar(): String {
        return UserDataService.avatarName
    }

    override fun getStoredMessages(): ArrayList<Message> {
        return mMessageService.messages
    }

    override fun getStoredChannels(): ArrayList<Channel> {
        return mMessageService.channels
    }

    override fun addMessage(newMessage: Message) {
        mMessageService.messages.add(newMessage)
    }


    override fun addNewChannel(newChannel: Channel) {
        mMessageService.channels.add(newChannel)
    }

    override fun clearMessages() {
        mMessageService.clearMessages()
    }

    override fun clearChannels() {
        mMessageService.clearChannels()
    }

    override fun foundMessages(response: JSONArray, complete: (Boolean) -> Unit) {
        try {
            mMessageService.clearMessages()
            for(x in 0 until response?.length()) {
                val message = response?.getJSONObject(x)
                val messageBody = message.getString("messageBody")
                val channelId = message.getString("channelId")
                val id = message.getString("_id")
                val userId = message.getString("userId")
                val userName = message.getString("userName")
                val userAvatar = message.getString("userAvatar")
                val userAvatarColor = message.getString("userAvatarColor")
                val timeStamp = message.getString("timeStamp")
                val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                addMessage(newMessage)
            }
            complete(true)
        } catch (e: JSONException){
            Log.e("MainMvpPresenter","Exception : ${e.printStackTrace()}")
            complete(false)
        }
    }
}