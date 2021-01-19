package com.contactsmanager.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi


class PhoneStateReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {

        context.sendBroadcast(Intent("action"))

        try {
            println("Receiver start")
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                val telephony = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

                Log.i("tag", "onReceive: $incomingNumber")
//                val mainFragment = FragmentManager.findFragment<MainFragment>(R.layout.fragment_main)
                if (incomingNumber.equals("+918527753545")) {
                    telephony.endCall()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}