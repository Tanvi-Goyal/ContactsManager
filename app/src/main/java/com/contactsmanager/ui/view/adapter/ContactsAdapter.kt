package com.contactsmanager.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contactsmanager.data.entities.Contact
import com.contactsmanager.databinding.ItemContactBinding

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    private var itemList = ArrayList<Contact>()
    var onItemClick: ((Contact) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : ItemContactBinding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = itemList[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int = itemList.size

    fun addContact(contacts: ArrayList<Contact>) {
        itemList.clear()
        itemList.addAll(contacts)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val itemContactBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemContactBinding.root) {

        fun bind(contact: Contact) {
            itemContactBinding.contact = contact
            itemContactBinding.executePendingBindings()

            itemContactBinding.nameIcon.letter = contact.cName

        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(itemList[adapterPosition])
            }
        }
    }
}