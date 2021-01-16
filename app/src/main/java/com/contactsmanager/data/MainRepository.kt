package com.contactsmanager.data

import com.contactsmanager.data.local.ContactsDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val contactsDao: ContactsDao
) {
}