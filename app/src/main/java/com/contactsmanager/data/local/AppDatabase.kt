package com.contactsmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contactsmanager.data.entities.Contact

@Database(
    entities = [Contact::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
}