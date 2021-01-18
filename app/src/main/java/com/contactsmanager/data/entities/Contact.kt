package com.contactsmanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_contacts")
data class Contact(
    @PrimaryKey
    val cNo: String,
    val cName: String = "Unknown"
)