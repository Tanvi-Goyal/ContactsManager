package com.contactsmanager.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contactsmanager.data.MainRepository
import com.contactsmanager.data.entities.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val blockedContacts = repository.getBlockedContacts()

    fun addToBlockedContacts(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToBlockedContacts(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteContact(contact)
        }
    }
}