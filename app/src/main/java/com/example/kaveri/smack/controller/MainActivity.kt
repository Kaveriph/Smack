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
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.example.kaveri.smack.R
import com.example.kaveri.smack.R.id.*
import com.example.kaveri.smack.services.AuthService
import com.example.kaveri.smack.services.UserDataService
import com.example.kaveri.smack.utilities.BASE_URL
import com.example.kaveri.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.kaveri.smack.utilities.SOCKET_URL
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    //create sockets -
    val socket = IO.socket(SOCKET_URL)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE) )
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
        socket.connect()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginButtonNavClicked(view:View) {
        if(AuthService.isLogedIn) {
            UserDataService.logout()
            userNameText.text = ""
            userEmailText.text = ""
            loginBtn.text = getString(R.string.login)
            userProfileImg.setImageResource(R.drawable.profiledefault)
            userProfileImg.setBackgroundColor(Color.TRANSPARENT)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelBtnClick(view:View) {
        if(AuthService.isLogedIn) {
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
        hideKeyboard()
    }

    var broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLogedIn) {
                userNameText.text  = UserDataService.name
                userEmailText.text = UserDataService.email
                loginBtn.text = resources.getString(R.string.logout)
                userProfileImg.setImageResource(resources.getIdentifier(UserDataService.avatarName,"drawable",packageName))
                userProfileImg.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
            }

        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }
}
