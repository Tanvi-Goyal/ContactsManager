package com.ui.view

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.contactsmanager.MainActivity
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.databinding.FragmentContactsBinding
import com.ui.MainViewModel
import com.ui.view.adapter.ContactsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var adapter: ContactsAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setListenerToBtn()
    }

    private fun setListenerToBtn() {
        binding.btnAdd.setOnClickListener {
            var name = binding.eAddName.text.toString()
            val phone = binding.eContactNo.text.toString()

            if(phone.isEmpty() || !isValidMobile(phone)) {
                Toast.makeText(requireContext(), "Please enter the correct phone number to proceed", Toast.LENGTH_SHORT).show()
            } else {
                if (name.isEmpty()) name = "Unknown"
                val contact = Contact(phone, name)
                viewModel.addToBlockedContacts(contact)
                Toast.makeText(requireContext(), "Number blocked", Toast.LENGTH_SHORT).show()
                binding.eAddName.text?.clear()
                binding.eContactNo.text?.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.contactsRecycler.layoutManager = LinearLayoutManager(context)
        adapter = ContactsAdapter()

        adapter.addContact(MainActivity.getContactList())
        binding.contactsRecycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.contactsRecycler.setHasFixedSize(true)
        binding.contactsRecycler.adapter = adapter

        adapter.onItemClick = { contact ->
            showAlert(contact)
        }
    }

    private fun showAlert(contact: Contact) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Block contact")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            viewModel.addToBlockedContacts(contact)
            Toast.makeText(requireContext(), "Number blocked", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }
}