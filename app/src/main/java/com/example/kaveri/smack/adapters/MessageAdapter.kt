package com.example.kaveri.smack.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.kaveri.smack.R
import com.example.kaveri.smack.model.Message
import com.example.kaveri.smack.services.UserDataService

/**
 * Created by KAVERI on 12/3/2017.
 */
class MessageAdapter(var context: Context, var messages:ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindMessage(context, messages.get(position))
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImg)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserNameLabel)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeSentLabel)
        val mesageBody = itemView?.findViewById<TextView>(R.id.messageBodyLabel)

        fun bindMessage(context: Context, message:Message) {
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = message.timeStamp
            mesageBody?.text = message.message

        }
    }

}