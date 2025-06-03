package com.example.mobile.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobile.R
import com.example.mobile.model.GroupRepository
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory


private const val ARG_GROUP = "groupId"

class GraphFragment : Fragment() {

    private var groupId: String? = null
    private lateinit var userCreditsTextView: TextView
    private lateinit var userDebtsTextView: TextView
    private lateinit var allGroupPaymentTextView: TextView

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }

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
        val view = inflater.inflate(R.layout.fragment_group_graphs, container, false)


        userCreditsTextView=view.findViewById(R.id.userCredits)
        userDebtsTextView=view.findViewById(R.id.userDebts)
        allGroupPaymentTextView=view.findViewById(R.id.totalGroupExpense)

        groupId?.let { groupViewModel.loadCredits(it) }
        groupId?.let { groupViewModel.loadDebts(it) }
        groupId?.let { groupViewModel.loadTotalExpense(it) }


        groupViewModel.creditsLiveData.observe(viewLifecycleOwner, Observer { credits ->
            val formattedAmount = String.format("%.2f", credits)
            userCreditsTextView.text = formattedAmount

        })

        groupViewModel.debtsLiveData.observe(viewLifecycleOwner, Observer { debts ->
            val formattedAmount = String.format("%.2f", debts)
            userDebtsTextView.text = formattedAmount

        })

        groupViewModel.totalGroupPayment.observe(viewLifecycleOwner, Observer { total ->
            val formattedAmount = String.format("%.2f", total)
            allGroupPaymentTextView.text= formattedAmount

        })


        return view
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
