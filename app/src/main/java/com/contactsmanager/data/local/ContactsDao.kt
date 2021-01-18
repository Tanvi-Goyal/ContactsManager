package com.contactsmanager.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contactsmanager.data.entities.Contact

@Dao
interface ContactsDao {

    @Query("SELECT * FROM blocked_contacts")
    fun getBlockedContacts(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addContact(contact: Contact)

    @Query("DELETE FROM blocked_contacts WHERE cNo = :cNumber")
    fun deleteContact(cNumber: String)
}