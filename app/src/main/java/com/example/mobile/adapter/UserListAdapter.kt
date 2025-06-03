package com.example.mobile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.model.User

class UserListAdapter(private var userList: List<User>, private var userBalances: Map<String, Double>) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val balanceTextView: TextView = view.findViewById(R.id.balanceTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

         val user = userList[position]
         val userName=user.name

         holder.userNameTextView.text=userName
        val balance = userBalances[user.id] ?: 0.0 // Ottieni il saldo corrispondente o 0.0 se non presente
        val formattedBalance = String.format("%.2f", balance) // Formatta il saldo come stringa

        holder.balanceTextView.text = "Balance: $formattedBalance"

         holder.itemView.setOnClickListener{

         }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateUserBalances(newBalances: Map<String, Double>) {
        userBalances = newBalances
        notifyDataSetChanged()
    }

}

