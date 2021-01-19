package com.contactsmanager.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi


class PhoneStateReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {

        try {
            println("Receiver start")
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                Log.i("tag", "onReceive: $incomingNumber")
                if (incomingNumber != null) {
                    context.sendBroadcast(
                        Intent("action").putExtra("number", incomingNumber)
                    )
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}