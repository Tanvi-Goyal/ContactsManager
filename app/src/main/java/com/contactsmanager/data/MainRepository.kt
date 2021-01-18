package com.contactsmanager.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.data.local.ContactsDao
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val contactsDao: ContactsDao
) {

    fun getBlockedContacts(): LiveData<Resource<List<Contact>>> = liveData(Dispatchers.IO) {
        emit(Resource.loading())

        val local = { contactsDao.getBlockedContacts() }.invoke().map { Resource.success(it) }
        emitSource(local)
    }

    fun addToBlockedContacts(contact: Contact) = contactsDao.addContact(contact)

    fun deleteContact(contact: Contact) = contactsDao.deleteContact(contact.cNo)
}