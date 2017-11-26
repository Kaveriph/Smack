package com.example.kaveri.smack.model

/**
 * Created by KAVERI on 11/26/2017.
 */
class Channel(val name:String, val description:String, val id:String) {
    override fun toString(): String {
        return "#$name"
    }
}