package com.example.mobile
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.mobile.databinding.FragmentCreateGroupBinding
import com.example.mobile.model.GroupRepository
import com.example.mobile.viewmodel.GroupViewModel
import com.example.mobile.viewmodel.GroupViewModelFactory


class CreateGroupFragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentCreateGroupBinding
    private val groupViewModel: GroupViewModel by viewModels() {
        GroupViewModelFactory(
            GroupRepository(), // Create your instances of repositories here

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = groupViewModel
        binding.createGroupButton.setOnClickListener {
            val groupName = binding.groupName.text.toString()
            val groupType = binding.groupType.text.toString()

            // Assicurati che i valori non siano vuoti o nulli prima di chiamare addGroup
            if (groupName.isNotEmpty() && groupType.isNotEmpty()) {
                groupViewModel.addGroup(groupName, groupType)
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                // Gestisci il caso in cui uno o entrambi i campi sono vuoti
                // Ad esempio, mostra un messaggio di errore all'utente
                Toast.makeText(
                    requireContext(),
                    "Inserire entrambi i campi.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        return binding.root
    }
}




