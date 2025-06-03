package com.example.mobile.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.model.Expense
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseListAdapter(private var expenseList:List<Expense>,
                         private val onExpenseClickListener: (Expense) -> Unit):

       RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>(){
           class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
               val expenseNameTextView: TextView = view.findViewById(R.id.expensenameTextView)
               val expenseAmountTextView: TextView = view.findViewById(R.id.expenseAmountTextView)
               val expenseDateTextView: TextView = view.findViewById(R.id.expenseDateTextView)


           }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        val expense = expenseList[position]
        val expenseName=expense.name
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(expense.date)

        holder.expenseDateTextView.text = formattedDate
        holder.expenseNameTextView.text=expenseName
        val formattedAmount = String.format("%.2f", expense.amount)

        holder.expenseAmountTextView.text = formattedAmount
        holder.itemView.setOnClickListener{

            onExpenseClickListener.invoke(expense)
        }

    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newExpense: List<Expense>) {
        expenseList = newExpense.sortedByDescending { it.date }
        Log.d("ExpenseListAdapter", "Updated expense list. Size: ${expenseList.size}")
        notifyDataSetChanged()
    }

}



