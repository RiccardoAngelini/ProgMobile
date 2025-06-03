package com.example.mobile.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.adapter.UserListAdapter
import com.example.mobile.model.GroupRepository
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory
import kotlinx.coroutines.launch

private const val ARG_GROUP = "groupId"

class UserFragment : Fragment() {

        private var groupId: String? = null
    private lateinit var addUserButton: Button

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }
        private  val userListAdapter= UserListAdapter(emptyList(), emptyMap())


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
            val view=inflater.inflate(R.layout.fragment_group_users, container, false)


            val userRecyclerView:RecyclerView=view.findViewById(R.id.userRecyclerView)
            userRecyclerView.adapter=userListAdapter
            userRecyclerView.layoutManager=LinearLayoutManager(requireContext())



             addUserButton = view.findViewById(R.id.newUserButton)



            addUserButton.setOnClickListener{
                val addUserFragment= AddUserFragment()

                val bundle = Bundle().apply {
                    putString(ARG_GROUP, groupId)
                }
                addUserFragment.arguments=bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, addUserFragment)
                    .addToBackStack(null)
                    .commit()
            }


            return view

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


            val bundle = Bundle().apply {
                putString(ARG_GROUP, groupId)
            }


                groupId?.also {
                    Log.d("UserFragment", "Loading users for group: $it")
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            groupViewModel.loadGroupUser(it)
                            groupViewModel.calculateUserBalances(it)
                        } catch (e: Exception) {
                            Log.e("UserFragment", "Errore durante il caricamento degli utenti", e)
                            // Gestisci l'errore in base alle tue esigenze
                        }
                    }
            }
            groupViewModel.userLiveData.observe(viewLifecycleOwner) { users ->
                Log.d("UserFragment", "Received users: $users")
                userListAdapter.updateUsers(users)
            }
            groupViewModel.userBalancesLiveData.observe(viewLifecycleOwner){
                userBalances->
                Log.d("UserFragment", "Received balance: $userBalances")
                userListAdapter.updateUserBalances(userBalances)
            }

            groupId?.let { groupViewModel.checkIfCurrentUserIsAdmin(it) }

            groupViewModel.isCurrentUserAdmin.observe(viewLifecycleOwner) { isAdmin ->
                if (isAdmin) {
                    addUserButton.visibility = View.VISIBLE
                } else {
                    addUserButton.visibility = View.GONE
                    // Puoi anche mostrare un messaggio o fare altre azioni se l'utente non è amministratore
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


