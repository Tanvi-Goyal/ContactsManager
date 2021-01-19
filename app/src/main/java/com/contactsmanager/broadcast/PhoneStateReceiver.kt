package com.contactsmanager.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.android.internal.telephony.ITelephony


class PhoneStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

//        val telephonyService: ITelephony

        try {
            println("Receiver start")
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
//
//            if (incomingNumber.equals("+918527753545")) {
//                resultData = null
//            }
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

               val telephonyService: Any
                val telephony =  context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                try{
                    var c = Class.forName(telephony.javaClass.name)
                    var m = c.getDeclaredMethod("getITelephony")
                    m.isAccessible = true
                    telephonyService = m.invoke(telephony) as Any
                    if (incomingNumber.equals("+917701897862")) {
                        c = Class.forName(telephonyService.javaClass.name) // Gets its class
                        m = c.getDeclaredMethod("endCall") // Get the "endCall()' method
                        m.isAccessible = true //Make it accessible
                        m.invoke(telephonyService) //invoke endCall()
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }

//                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
////                try {
//                    val m = tm.javaClass.getDeclaredMethod("getITelephony")
//                    m.isAccessible = true
//                    telephonyService = m.invoke(tm) as ITelephony
//                    if (incomingNumber.equals("+917701897862")) {
//                        telephonyService.silenceRinger();
//                        telephonyService.endCall()
//                    }

//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }

                Toast.makeText(context, "Number is -$incomingNumber", Toast.LENGTH_SHORT).show();

            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
//                Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
//            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}