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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.adapter.ExpenseListAdapter
import com.example.mobile.model.Expense
import com.example.mobile.model.GroupRepository
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory


private const val ARG_GROUP = "groupId"

class ExpenseFragment : Fragment() {
    private var groupId: String? = null

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }
    private lateinit var expenseListAdapter: ExpenseListAdapter


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
        val view = inflater.inflate(R.layout.fragment_group_expense, container, false)

        expenseListAdapter= ExpenseListAdapter(emptyList()){
            expense->
            showAddExpenseDialog(expense)
        }
        val expenseRecyclerView: RecyclerView =view.findViewById(R.id.expenseRecyclerView)
        expenseRecyclerView.adapter=expenseListAdapter
        expenseRecyclerView.layoutManager= LinearLayoutManager(requireContext())


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

    @SuppressLint("SetTextI18n")
    private fun showAddExpenseDialog(expense: Expense) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Expense deatils")

        // Aggiungi un TextView personalizzato al layout del dialog
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_expense, null)
        alertDialogBuilder.setView(dialogLayout)

        val num = expense.debtors.size
        val payment = expense.amount*num

        val expenseNameTextView: TextView = dialogLayout.findViewById(R.id.expenseNameTextView)
        val payerTextView: TextView = dialogLayout.findViewById(R.id.payerTextView)
        val debtorsTextView: TextView = dialogLayout.findViewById(R.id.debtorsTextView)

        val formattedAmount = String.format("%.2f", expense.amount)

        expenseNameTextView.text= expense.name

        groupViewModel.getUserName(expense.payer)
        groupViewModel.userNameLiveData.observe(viewLifecycleOwner) { payerName ->
            payerTextView.text = "${payerName} payed ${payment}"
        }

        groupViewModel.getDebtorNames(expense.debtors)

        groupViewModel.debtorNamesLiveData.observe(viewLifecycleOwner) { debtorNames ->

            debtorsTextView.text = "${debtorNames.joinToString(", ")} owed ${formattedAmount} "
        }




        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Gestisci il clic sul pulsante OK se necessario
        }
        alertDialogBuilder.show()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        groupId?.also {
            Log.d("ExpenseFragment", "Loading expense for user in group: $it")
            groupViewModel.loadExpenseGroup(it)
        }

        groupViewModel.expenseLive.observe(viewLifecycleOwner) { expense ->
            Log.d("ExpenseFragment", "Received expense: $expense")
            expenseListAdapter.updateUsers(expense)
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
