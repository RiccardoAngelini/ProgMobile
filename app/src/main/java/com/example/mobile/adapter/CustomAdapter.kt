package com.example.mobile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.model.Group


class CustomAdapter(private var dataSet: List<Group>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameTextView: TextView = view.findViewById(R.id.textView)
        val groupTypeText: TextView = view.findViewById(R.id.groupType)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_viewgroup, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val group = dataSet[position]
        val groupName= group.name
        val groupType = group.type
        viewHolder.groupNameTextView.text = groupName
        viewHolder.groupTypeText.text = groupType


        viewHolder.itemView.setOnClickListener {
            // Richiama la funzione di callback con l'id del gruppo quando viene cliccato
            onItemClickListener.invoke(group.id)
        }
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Group>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}



