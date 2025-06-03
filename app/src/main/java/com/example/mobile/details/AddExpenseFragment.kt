package com.example.mobile.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.adapter.SpinnerUserAdapter
import com.example.mobile.adapter.SplitUsersAdapter
import com.example.mobile.model.GroupRepository
import com.example.mobile.model.User
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory
import java.util.Calendar


private const val ARG_GROUP = "groupId"


class AddExpenseFragment : Fragment() {

    private var groupId: String? = null

    private lateinit var expenseNameEditText: EditText
    private lateinit var expenseAmountEditText: EditText

    private lateinit var spinnerPayer: Spinner
    private lateinit var recyclerViewSplitUsers: RecyclerView
    private lateinit var buttonAddExpense: Button
    private lateinit var spinnerUserAdapter:SpinnerUserAdapter
    private lateinit var splitUsersAdapter: SplitUsersAdapter
    private lateinit var selectedUser: User
    private lateinit var debtors:List<User>

    private lateinit var groupViewModel: GroupViewModel


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

        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        groupViewModel = ViewModelProvider(this, GroupViewModelFactory(
            GroupRepository(),
        )
        ).get(GroupViewModel::class.java)

         expenseNameEditText = view.findViewById(R.id.editTextExpenseName)
         expenseAmountEditText = view.findViewById(R.id.editTextExpenseAmount)

         spinnerPayer = view.findViewById(R.id.spinnerPayer)
         buttonAddExpense= view.findViewById(R.id.buttonAddExpense)
         recyclerViewSplitUsers = view.findViewById(R.id.recyclerViewSplitUsers)

        splitUsersAdapter = SplitUsersAdapter(emptyList())
        recyclerViewSplitUsers.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSplitUsers.adapter = splitUsersAdapter


        spinnerUserAdapter = SpinnerUserAdapter(requireContext(), emptyList())
        spinnerPayer.adapter = spinnerUserAdapter

        spinnerPayer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val user = spinnerUserAdapter.getItem(p2)
                if (user != null) {
                    selectedUser = user
                    Log.d("AddExpenseFragment", "Utente selezionato dallo spinner: $selectedUser")
                } else {
                    // Gestisci il caso in cui getItem restituisce null
                    Log.e("AddExpenseFragment", "Errore: getItem restituisce null")
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

                Log.d("AddExpenseFragment", "Nessun utente selezionato dallo spinner")
            }

        }


        buttonAddExpense.setOnClickListener {

            val expenseName = expenseNameEditText.text.toString()
            val expenseAmount = expenseAmountEditText.text.toString().toDoubleOrNull()


            debtors= splitUsersAdapter.getSelectedUsers()

            Log.d("AddExpenseFragment", "Utenti selezionati dalla checkbox: $debtors")
            Log.d("AddExpenseFragment", "Utente selezionato dallo spinner: $selectedUser")

            if (expenseName.isNotBlank() && expenseAmount != null )   {

                if(debtors.isEmpty()){ //aggiungi la condizione per la checkbox e mettila a posto
                    Toast.makeText(requireContext(), "Select one or more debtors.", Toast.LENGTH_LONG).show()
                } else{

                    groupId?.let { it1 ->
                        val currentDate = Calendar.getInstance().time
                        groupViewModel.addExpense(debtors,
                            it1,expenseName,expenseAmount , selectedUser,currentDate )
                    }


                    Toast.makeText(
                        requireContext(),
                        "Expense added.",
                        Toast.LENGTH_LONG
                    ).show()

                    splitUsersAdapter.clearSelectedUsers()

            } }else {
                Toast.makeText(
                    requireContext(),
                    "Fill in the blank fields.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        groupId?.also {
            Log.d("UserFragment", "Loading users for group: $it")
            groupViewModel.loadGroupUser(it)
        }

        groupViewModel.userLiveData.observe(viewLifecycleOwner){
                newUsers ->
            if (newUsers.isNotEmpty()) {
                spinnerUserAdapter.updateUsers(newUsers)
                splitUsersAdapter.updateUsers(newUsers)
                Log.d("UserFragment", "Updated users in adapters")
            } else {
                Log.d("UserFragment", "No new users data")
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
            AddExpenseFragment().apply {
                arguments = Bundle().apply {
                    putString( ARG_GROUP, groupId)

                }
            }
    }
}


