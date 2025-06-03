package com.example.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.adapter.CustomAdapter
import com.example.mobile.model.GroupRepository
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory

class ListGroupFragment : Fragment() {

    private val customAdapter = CustomAdapter(emptyList()) { groupId ->
        // Quando un elemento viene cliccato, groupId contiene l'id del gruppo
        // Esegui l'azione desiderata qui
        val detailsGroupFragment = DeatilsGroupFragment()

        // Passa l'ID del gruppo utilizzando un Bundle
        val bundle = Bundle().apply {
            putString("groupId", groupId)
        }

        detailsGroupFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailsGroupFragment)
            .addToBackStack(null)
            .commit()
    }

    private val groupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(GroupRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_group, container, false)


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.setHasFixedSize(true)

        val newGroupButton: Button = view.findViewById(R.id.newGroupButton)


        // Aggiungi un OnClickListener al pulsante
        newGroupButton.setOnClickListener {
            // Quando il pulsante viene cliccato, apri il fragment di creazione del gruppo
            val createGroupFragment = CreateGroupFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, createGroupFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupViewModel.groupLive.observe(viewLifecycleOwner) { groups ->

            customAdapter.updateData(groups)
        }

        groupViewModel.loadGroup()
    }
}





