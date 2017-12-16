package com.example.kaveri.smack.views.interfaces

import android.view.View
import com.example.kaveri.smack.model.Channel
import io.socket.emitter.Emitter

/**
 * Created by KAVERI on 12/16/2017.
 */
interface MainMvpView :MvpView {

    fun loginButtonNavClicked(view:View)
    fun addChannelBtnClick(view:View)
    fun onNewChannel(): Emitter.Listener
    fun onNewMessage(): Emitter.Listener
    fun sendMessageBtnClicked(view:View)
    fun foundChannels(foundChannels: Boolean, channels:ArrayList<Channel>)
    fun foundMessages(success:Boolean)

}