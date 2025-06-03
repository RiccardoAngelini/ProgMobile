package com.example.mobile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.model.User
class SplitUsersAdapter(private var userList: List<User>) :
    RecyclerView.Adapter<SplitUsersAdapter.UserViewHolder>() {

    private var selectedUsers: MutableList<User> = mutableListOf()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.usernameCheckbox)
        val userCheckBox: CheckBox = itemView.findViewById(R.id.userCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_split, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        val userName = user.name
        holder.userNameTextView.text = userName

        // Imposta uno stato coerente per la checkbox
        holder.userCheckBox.isChecked = selectedUsers.contains(user)

        // Utilizza l'evento sul cambio di stato della checkbox per gestire la selezione/deselezione degli utenti
        holder.userCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedUsers.add(user)
            } else {
                selectedUsers.remove(user)
            }
        }
    }

    override fun getItemCount(): Int = userList.size

    // Restituisci la lista degli utenti selezionati
    fun getSelectedUsers(): List<User> {
        return selectedUsers.toList()
    }

    // Pulisci la lista degli utenti selezionati
    fun clearSelectedUsers() {
        selectedUsers.clear()
    }

    // Aggiorna la lista degli utenti
    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }
}
