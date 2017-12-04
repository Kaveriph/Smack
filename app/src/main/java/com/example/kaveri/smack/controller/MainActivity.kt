package com.example.kaveri.smack.controller

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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.kaveri.smack.R
import com.example.kaveri.smack.adapters.MessageAdapter
import com.example.kaveri.smack.model.Channel
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.services.AuthService
import com.example.kaveri.smack.services.MessageService
import com.example.kaveri.smack.services.UserDataService
import com.example.kaveri.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.kaveri.smack.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG :String= "MainActivity"

    //create sockets -
    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<Channel>
    var selectedChannel : Channel? = null
    lateinit var messageAdapter : MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE) )

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        setupAdapters()
        channel_list.setOnItemClickListener{_, _, i, _ ->
            selectedChannel = MessageService.channels.get(i)
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
        if(App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this){}
        }
    }

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
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

    fun loginButtonNavClicked(view:View) {
        if(App.prefs.isLoggedIn) {
            UserDataService.logout()
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

    fun addChannelBtnClick(view:View) {
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
                socket.emit("newChannel",channelName, channelDescription)
            }
            builder.setNegativeButton("Cancel") {dialogInterface, i ->
                //cancel or closethe dialog
            }
            builder.show()
        }
    }

    fun sendMessageBtnClicked(view:View) {

        if(App.prefs.isLoggedIn && messageText.text.toString().isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel?.id
            socket.emit("newMessage",messageText.text.toString(),userId, channelId,
                    UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageText.text.clear()
            hideKeyboard()
        }
    }

    var broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.prefs.isLoggedIn) {
                userNameText.text  = UserDataService.name
                userEmailText.text = UserDataService.email
                loginBtn.text = resources.getString(R.string.logout)
                userProfileImg.setImageResource(resources.getIdentifier(UserDataService.avatarName,"drawable",packageName))
                userProfileImg.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                MessageService.getChannels({ foundChannels ->
                    if(foundChannels) {
                        if(MessageService.channels.size > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }

                })
            }

        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${ selectedChannel?.name}"

        if(selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) { success ->
                if(success) {
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        socket.disconnect()
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
                MessageService.channels.add(newChannel)
                println("${newChannel.name} ${newChannel.description} ${newChannel.id}")
                channelAdapter.notifyDataSetChanged()
            }
        }
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
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

}
