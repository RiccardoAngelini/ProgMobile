package com.example.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobile.databinding.FragmentDeatilsGroupBinding
import com.example.mobile.details.ExpenseFragment
import com.example.mobile.details.GraphFragment
import com.example.mobile.details.PayerFragment
import com.example.mobile.details.UserFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val ARG_GROUP_ID = "groupId"

class DeatilsGroupFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters

    private var groupId: String? = null
    private lateinit var binding:FragmentDeatilsGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            groupId = it.getString(ARG_GROUP_ID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentDeatilsGroupBinding.inflate(inflater,container,false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState:  Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.UserButton.setOnClickListener {
            val userFragment = UserFragment()

            val bundle = Bundle().apply {
                putString(ARG_GROUP_ID, groupId)
            }

            userFragment.arguments = bundle

            // Utilizza il FragmentManager per sostituire o aggiungere il fragment successivo
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, userFragment) // R.id.fragment_container è l'ID del tuo FrameLayout
            transaction.addToBackStack(null) // Se desideri aggiungere la transazione allo stack indietro
            transaction.commit()


    }
        binding.ExpenseButton.setOnClickListener{
            val expenseFragment = ExpenseFragment()

            val bundle = Bundle().apply {
                putString(ARG_GROUP_ID, groupId)
            }

            expenseFragment.arguments = bundle

            // Utilizza il FragmentManager per sostituire o aggiungere il fragment successivo
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expenseFragment) // R.id.fragment_container è l'ID del tuo FrameLayout
            transaction.addToBackStack(null) // Se desideri aggiungere la transazione allo stack indietro
            transaction.commit()
        }

        binding.PaymentButton.setOnClickListener{
            val paymentFragment= PayerFragment()
            val bundle = Bundle().apply {
                putString(ARG_GROUP_ID, groupId)
            }

            paymentFragment.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, paymentFragment) // R.id.fragment_container è l'ID del tuo FrameLayout
            transaction.addToBackStack(null) // Se desideri aggiungere la transazione allo stack indietro
            transaction.commit()
        }

        binding.GraphButton.setOnClickListener{
            val graphFragment= GraphFragment()
            val bundle = Bundle().apply{
                putString(ARG_GROUP_ID,groupId)
        }
            graphFragment.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, graphFragment) // R.id.fragment_container è l'ID del tuo FrameLayout
            transaction.addToBackStack(null) // Se desideri aggiungere la transazione allo stack indietro
            transaction.commit()
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
            DeatilsGroupFragment().apply {
                arguments = Bundle().apply {
                    putString( ARG_GROUP_ID, groupId)

                }
            }
    }
}