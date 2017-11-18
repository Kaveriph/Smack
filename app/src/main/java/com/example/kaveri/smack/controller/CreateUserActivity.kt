package com.example.kaveri.smack.controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kaveri.smack.R
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var userAvatar= "profileDefault"
    private var avatarColor = "[0.5, 0.5, 0.5, 1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateBgColor(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        signupUserAvatarImg.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r/255
        val savedG = g/255
        val savedB = b/255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun onTapToGenerateAvatar(view:View) {

        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = when(color) {
            0 -> "light$avatar"
            else -> "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar,"drawable", this.packageName)
        signupUserAvatarImg.setImageResource(resourceId)
    }

    fun createUserClicked(view:View) {

    }
}
