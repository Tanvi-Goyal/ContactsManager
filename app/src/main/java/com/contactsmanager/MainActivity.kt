package com.contactsmanager

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        private var contactsList = ArrayList<Contact>()

        fun getContactList(): ArrayList<Contact> = contactsList
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadContacts()
    }

    private fun loadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            GlobalScope.launch {
                getContacts()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(
                    this,
                    "Permission must be granted in order to display contacts information",
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