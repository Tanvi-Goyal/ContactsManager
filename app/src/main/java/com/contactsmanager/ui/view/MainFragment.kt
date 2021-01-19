package com.contactsmanager.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.contactsmanager.R
import com.contactsmanager.data.Resource
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.databinding.FragmentMainBinding
import com.contactsmanager.ui.MainViewModel
import com.contactsmanager.ui.view.adapter.ContactsAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: ContactsAdapter
    private val viewModel: MainViewModel by viewModels()

//    private val mYourBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            // Now you can call all your fragments method here
//            Log.i("TAG", "onReceive: B received")
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
//            mYourBroadcastReceiver, IntentFilter("action")
//        )
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
//            mYourBroadcastReceiver, IntentFilter("action")
//        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        LocalBroadcastManager.getInstance(requireContext())
//            .unregisterReceiver(mYourBroadcastReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setViewModelObservers()
        binding.addContact.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.navigate_to_contacts)
        }
    }

    private fun setupRecyclerView() {
        binding.contactsRecycler.layoutManager = LinearLayoutManager(context)
        adapter = ContactsAdapter()

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
        builder.setMessage("Unblock contact")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            viewModel.deleteContact(contact)
            Toast.makeText(requireContext(), "Number unblocked", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun setViewModelObservers() {
        viewModel.blockedContacts.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        adapter.addContact(it.data as ArrayList<Contact>)
//                        binding.progressbar.visibility = View.GONE
                    }

                }
                Resource.Status.ERROR -> {
                    Log.e("TAG", "setViewModelObservers: error")
//                    binding.progressbar.visibility = View.GONE
                }

                Resource.Status.LOADING -> {
//                    binding.progressbar.visibility = View.VISIBLE
                    Log.e("TAG", "setViewModelObservers: loading")
                }
            }
        })
    }

}