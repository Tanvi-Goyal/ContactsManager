package com.contactsmanager.ui.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.contactsmanager.MainActivity
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
    private lateinit var blockedList : ArrayList<Contact>

    companion object {
        const val CHANNEL_ID = "123"
        const val NOTIFICATION_ID = 111
    }

    private var mYourBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context, intent: Intent) {

            Log.i("TAG", "onReceive: Call in fragment received")
            val number = intent.getStringExtra("number")
            val telephony = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

            if(blockedList.size > 0) {
                blockedList.forEach { contact ->
                    if (number.equals(contact.cNo)) {
                        telephony.endCall()
                        displayNotification(number!!)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        requireActivity().registerReceiver(mYourBroadcastReceiver, IntentFilter("action"))
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
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
        builder.setMessage(getString(R.string.title_unblock_contact))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            viewModel.deleteContact(contact)
            Toast.makeText(requireContext(), getString(R.string.number_unblocked), Toast.LENGTH_SHORT).show()
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
                    binding.progressBar.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) {
                        binding.textNoContacta.visibility = View.GONE
                        binding.contactsRecycler.visibility = View.VISIBLE
                        blockedList = it.data as ArrayList<Contact>
                        adapter.addContact(blockedList)
                    } else {
                        binding.textNoContacta.visibility = View.VISIBLE
                        binding.contactsRecycler.visibility = View.GONE
                    }

                }
                Resource.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("TAG", "setViewModelObservers: error")
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.e("TAG", "setViewModelObservers: loading")
                }
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun displayNotification(number: String){
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(getString(R.string.call_alert))
            .setContentText("Call from the blocked contact $number disconnected.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

}