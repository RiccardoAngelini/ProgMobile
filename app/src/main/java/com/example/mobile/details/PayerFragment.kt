package com.example.mobile.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.adapter.PaymentListAdapter
import com.example.mobile.model.GroupRepository
import com.example.mobile.model.Payment
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory
import kotlinx.coroutines.launch


private const val ARG_GROUP = "groupId"

class PayerFragment: Fragment() {
    private var groupId: String? = null

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }

    private lateinit var paymentListAdapter: PaymentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            groupId = it.getString(ARG_GROUP)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_group_payment, container, false)

        paymentListAdapter= PaymentListAdapter(emptyList()){
            payment ->  showAddPaymentDialog(payment)
        }
        val paymentRecyclerView: RecyclerView=view.findViewById(R.id.paymentRecyclerView)
        paymentRecyclerView.adapter= paymentListAdapter
        paymentRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        val addExpenseButton: Button = view.findViewById(R.id.newExpenseButton)
        addExpenseButton.setOnClickListener {
            val addExpenseFragment = AddExpenseFragment()

            val bundle = Bundle().apply {
                putString(ARG_GROUP, groupId)
            }
            addExpenseFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addExpenseFragment)
                .addToBackStack(null)
                .commit()
        }


return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId?.also {
            Log.d("ExpenseFragment", "Loading expense for user in group: $it")
            groupViewModel.loadGroupPayments(it)
        }

        groupViewModel.paymentLive.observe(viewLifecycleOwner){
            payment->
            Log.d("PaymentFragment", "Received payment: $payment")
            paymentListAdapter.updateUsers(payment)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showAddPaymentDialog(payment: Payment) {
        lifecycleScope.launch {
            try {
                val expense = groupViewModel.fetchExpenseByPayment(payment)

                if (expense != null) {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("Payment details")
                    val dialogLayout = layoutInflater.inflate(R.layout.dialog_expense, null)
                    alertDialogBuilder.setView(dialogLayout)

                    val paymentNameTextView:TextView=dialogLayout.findViewById(R.id.expenseNameTextView)
                    val payerTextView: TextView = dialogLayout.findViewById(R.id.payerTextView)
                    val debtorsTextView: TextView = dialogLayout.findViewById(R.id.debtorsTextView)

                    val num = expense.debtors.size
                    val paid = expense.amount*num

                    val formattedAmount = String.format("%.2f", expense.amount)

                    paymentNameTextView.text=payment.name

                    groupViewModel.getUserName(expense.payer)
                    groupViewModel.userNameLiveData.observe(viewLifecycleOwner) { payerName ->
                        payerTextView.text = "$payerName payed $paid"
                    }

                    groupViewModel.getDebtorNames(expense.debtors)

                    groupViewModel.debtorNamesLiveData.observe(viewLifecycleOwner) { debtorNames ->

                        debtorsTextView.text = "${debtorNames.joinToString(", ")} owed ${formattedAmount} "
                    }


                    alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                        // Gestisci il clic sul pulsante OK se necessario
                    }
                    alertDialogBuilder.show()
                } else {
                    Log.d("PayerFragment", "Errore durante il recupero dell'Expense")

                }
            } catch (e: Exception) {
                Log.e("PayerFragment", "Errore durante il caricamento del payment", e)
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment DeatilsGroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(groupId:String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString( ARG_GROUP, groupId)

                }
            }
    }

}