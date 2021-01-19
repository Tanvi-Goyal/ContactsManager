package com.contactsmanager

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.databinding.ActivityMainBinding
import com.contactsmanager.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var telephony: TelecomManager
//    private val viewModel: MainViewModel by viewModels()

//    private lateinit var incomingNo: String

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        private var contactsList = ArrayList<Contact>()

        fun getContactList(): ArrayList<Contact> = contactsList
    }

//    private val mYourBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//        override fun onReceive(context: Context, intent: Intent) {
////            context.sendBroadcast(Intent("action"))
//            Log.i("TAG", "onReceive: Activity received")
//
//            try {
//                println("Receiver start")
//                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
//                incomingNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).toString()
//
//                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    telephony = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
//
//                    Log.i("tag", "onReceive: $incomingNo")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        registerReceiver(mYourBroadcastReceiver, IntentFilter("action"))

//        setViewModelObservers()
        loadContacts()
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mYourBroadcastReceiver)
    }

    private fun loadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            GlobalScope.launch {
                getContacts()
            }
        }
    }

//    private fun setViewModelObservers() {
//        viewModel.blockedContacts.observe(this, {
//            when (it.status) {
//                Resource.Status.SUCCESS -> {
//                    if (!it.data.isNullOrEmpty()) {
//
//                        it.data.forEach { contact ->
//                            if(incomingNo == contact.cNo)  {
//                                telephony.endCall()
//                            }
//                        }
////                        binding.progressbar.visibility = View.GONE
//                    }
//
//                }
//                Resource.Status.ERROR -> {
//                    Log.e("TAG", "setViewModelObservers: error")
////                    binding.progressbar.visibility = View.GONE
//                }
//
//                Resource.Status.LOADING -> {
////                    binding.progressbar.visibility = View.VISIBLE
//                    Log.e("TAG", "setViewModelObservers: loading")
//                }
//            }
//        })
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                loadContacts()
            } else {
                Toast.makeText(
                    this,
                    "Permissions must be granted in order to display contacts information",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getContacts() {
        val resolver: ContentResolver = contentResolver
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone?.count!! > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                            Log.e("Name ===>", "$name -> $phoneNumValue")
                            contactsList.add(Contact(phoneNumValue, name))
                        }
                    }

                    cursorPhone.close()
                }
            }
        } else {
            Toast.makeText(this, "No contacts available", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}