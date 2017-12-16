package com.example.kaveri.smack.views.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.kaveri.smack.R
import com.example.kaveri.smack.adapters.MessageAdapter
import com.example.kaveri.smack.controller.App
import com.example.kaveri.smack.controller.LoginActivity
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.presenter.MainMvpPresenter
import com.example.kaveri.smack.services.MessageService
import com.example.kaveri.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.kaveri.smack.views.interfaces.MainMvpView
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), MainMvpView {


    val TAG :String= "MainActivity"

    //create sockets -
    lateinit var channelAdapter : ArrayAdapter<Channel>
    var selectedChannel : Channel? = null
    lateinit var messageAdapter : MessageAdapter
    private lateinit var mMainMvpPresenter: MainMvpPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setListeners()
        initData()
    }

    override fun initData() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE) )
        mMainMvpPresenter.createSocket()
        if(App.prefs.isLoggedIn) {
            mMainMvpPresenter.findUserByEmail(this)
        }
    }

    override fun init() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        mMainMvpPresenter = MainMvpPresenter(this)
        setupAdapters()
    }

    override fun setListeners() {
        channel_list.setOnItemClickListener{_, _, i, _ ->
            selectedChannel = mMainMvpPresenter.getStoredChannels().get(i)
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
    }


    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mMainMvpPresenter.getStoredChannels())
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, mMainMvpPresenter.getStoredMessages())
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun loginButtonNavClicked(view:View) {
        if(App.prefs.isLoggedIn) {
            mMainMvpPresenter.logOut()
            userNameText.text = ""
            userEmailText.text = ""
            loginBtn.text = getString(R.string.login)
            userProfileImg.setImageResource(R.drawable.profiledefault)
            userProfileImg.setBackgroundColor(Color.TRANSPARENT)
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            mainChannelName.text = getString(R.string.please_login_in)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    override fun addChannelBtnClick(view:View) {
        if(App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialogue, null)
            builder.setView(dialogView).create()
            builder.setPositiveButton("Add") {dialogInterface, i ->
                //add the channel
                val channelNameTxt = dialogView.findViewById<EditText>(R.id.addchannel_name_txt)
                val channelDescText = dialogView.findViewById<EditText>(R.id.addchannel_desc_txt)
                val channelName = channelNameTxt.text.toString()
                val channelDescription = channelDescText.text.toString()
                mMainMvpPresenter.getSocket().emit("newChannel",channelName, channelDescription)
            }
            builder.setNegativeButton("Cancel") {dialogInterface, i ->
                //cancel or closethe dialog
            }
            builder.show()
        }
    }

    override fun sendMessageBtnClicked(view:View) {

        if(App.prefs.isLoggedIn && messageText.text.toString().isNotEmpty() && selectedChannel != null) {
            val userId = mMainMvpPresenter.getUserId()
            val channelId = selectedChannel?.id
            mMainMvpPresenter.getSocket().emit("newMessage",messageText.text.toString(),userId, channelId,
                    mMainMvpPresenter.getUserName(), mMainMvpPresenter.getUserAvatar(), mMainMvpPresenter.getAvatarColor())
            messageText.text.clear()
            hideKeyboard()
        }
    }

    var broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.prefs.isLoggedIn) {
                userNameText.text  = mMainMvpPresenter.getUserName()
                userEmailText.text = mMainMvpPresenter.getUserEmail()
                loginBtn.text = resources.getString(R.string.logout)
                userProfileImg.setImageResource(resources.getIdentifier(mMainMvpPresenter.getUserAvatar(),"drawable",packageName))
                userProfileImg.setBackgroundColor(mMainMvpPresenter.getAvatarColorInRgb())

                mMainMvpPresenter.getChannels()
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${ selectedChannel?.name}"

        if(selectedChannel != null) {
           mMainMvpPresenter.getMessages(selectedChannel!!)
        }
    }

    override fun onDestroy() {
        mMainMvpPresenter.getSocket().disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }

    private val onNewChannel = Emitter.Listener{ args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                println(args[0] as String)
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String
                val newChannel = Channel(channelName, channelDescription, channelId)
                mMainMvpPresenter.addNewChannel(newChannel)
                println("${newChannel.name} ${newChannel.description} ${newChannel.id}")
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onNewChannel(): Emitter.Listener {
        return onNewChannel
    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                val messageBody = args[0] as String
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id) {
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String
                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    mMainMvpPresenter.addMessage(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onNewMessage(): Emitter.Listener {
        return onNewMessage
    }

    override fun foundChannels(foundChannels: Boolean, channels: ArrayList<Channel>) {
        if(foundChannels) {
            if(channels.size > 0) {
                selectedChannel = channels[0]
                channelAdapter.notifyDataSetChanged()
                updateWithChannel()
            }
        }
    }

    override fun foundMessages(success:Boolean) {
        if(success) {
            messageAdapter.notifyDataSetChanged()
            if(messageAdapter.itemCount > 0) {
                messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }
    }
}
