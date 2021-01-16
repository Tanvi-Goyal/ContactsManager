package com.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.contactsmanager.data.MainRepository

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

}