package com.contactsmanager.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.internal.telephony.ITelephony


class PhoneStateReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {

        try {
            println("Receiver start")
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                val telephony =  context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

                Log.i("tag", "onReceive: $incomingNumber")

                if (incomingNumber.equals("+919958494605")) {
                    telephony.endCall()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}