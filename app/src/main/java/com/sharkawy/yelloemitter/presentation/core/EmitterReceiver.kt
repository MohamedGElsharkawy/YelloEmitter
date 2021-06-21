package com.sharkawy.yelloemitter.presentation.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences.Editor


class EmitterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.hasExtra("MiddleManUser")) {
            val user = intent.getStringExtra("MiddleManUser")
            val prefs = context!!.getSharedPreferences(
                "YelloPref",
                MODE_PRIVATE
            )
            val editor: Editor = prefs.edit()
            editor.putString("Status", user)
            editor.apply()
        }

    }
}