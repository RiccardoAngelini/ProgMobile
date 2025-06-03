package com.example.mobile.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.adapter.AddUserListAdapter
import com.example.mobile.model.GroupRepository
import com.example.mobile.model.User
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


private const val ARG_GROUP = "groupId"


class AddUserFragment : Fragment() {

    private var groupId: String? = null

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }

    private lateinit var addUserListAdapter: AddUserListAdapter


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
        val view = inflater.inflate(R.layout.fragment_add_users, container, false)
        val userRecyclerView: RecyclerView = view.findViewById(R.id.addUserRecyclerView)
       
        addUserListAdapter = AddUserListAdapter(emptyList()) { user ->
            showAddUserDialog(user)
        }

        userRecyclerView.adapter = addUserListAdapter
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())

       return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bundle = Bundle().apply {
            putString(ARG_GROUP, groupId)
        }

        groupId?.also {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    Log.d("AddUserFragment", "Loading NOUsers for group: $it")
                    groupViewModel.loadNoGroupUser(it)
                } catch (e: CancellationException) {


                }
            }
        }

        groupViewModel.userLiveData.observe(viewLifecycleOwner) { users ->
            Log.d("UserFragment", "Received users: $users")
            addUserListAdapter.updateUsers(users)
        }
    }

    private fun showAddUserDialog(user: User) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle("Add User")
        alertDialogBuilder.setMessage("Add ${user.name} to group?")

        alertDialogBuilder.setPositiveButton("Confirm") { dialog, _ ->


            groupId?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        Log.d("AddUserFragment", "Trying to addUser: $it")
                        groupViewModel.addUser(it, user)
                    } catch (e: CancellationException) {
                        Log.d("AddUserFragment", "Impossible to addUser: $it")
                    }
                }
                }

            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
            AddUserFragment().apply {
                arguments = Bundle().apply {
                    putString( ARG_GROUP, groupId)

                }
            }
    }
}
