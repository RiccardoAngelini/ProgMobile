package com.example.mobile.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mobile.model.User

class SpinnerUserAdapter(
    context: Context,
    userList: List<User>
) : ArrayAdapter<User>(context, android.R.layout.simple_spinner_dropdown_item, userList.toMutableList()) {


    // Override del metodo per indicare la vista da visualizzare per ogni elemento nello Spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val user = getItem(position)
        view.findViewById<TextView>(android.R.id.text1)?.text = user?.name
        return view
    }

    // Override del metodo per indicare la vista da visualizzare quando si espande lo Spinner
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val user = getItem(position)
        view.findViewById<TextView>(android.R.id.text1)?.text = user?.name
        return view
    }

        fun updateUsers(newUsers: List<User>) {
            val mutableUserList = newUsers.toMutableList()
            Log.d("SpinnerUserAdapter", "Updated user list: $mutableUserList")
            clear()
            addAll(mutableUserList)
            notifyDataSetChanged()
        }

    }


