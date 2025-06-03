package com.example.mobile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.model.Payment
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentListAdapter (private var paymentList:List<Payment>,
                          private val onExpenseClickListener: (Payment) -> Unit):
    RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder>(){
    class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentNameTextView: TextView = view.findViewById(R.id.paymentNameTextView)
        val paymentAmountTextView: TextView = view.findViewById(R.id.paymentAmountTextView)
        val paymentDateTextView: TextView = view.findViewById(R.id.paymentDateTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListAdapter.PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
        return PaymentListAdapter.PaymentViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PaymentListAdapter.PaymentViewHolder, position: Int) {

        val payment = paymentList[position]
        val paymentName=payment.name
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(payment.date)

        holder.paymentDateTextView.text=formattedDate
        holder.paymentNameTextView.text=paymentName
        val formattedAmount = String.format("%.2f", payment.amount)

        holder.paymentAmountTextView.text = formattedAmount
        holder.itemView.setOnClickListener{

            onExpenseClickListener.invoke(payment)
        }

    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newPayment: List<Payment>) {
        paymentList = newPayment.sortedByDescending { it.date }
        notifyDataSetChanged()
    }
}

