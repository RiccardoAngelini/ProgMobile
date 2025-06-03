package com.example.mobile.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mobile.ListGroupFragment
import com.example.mobile.R
import com.google.firebase.auth.FirebaseAuth

    //CAPIRE SE RENDERE LOGIN UN ACTIVITY COSI DA GESTIRE ONSTART
    /* public override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null)
    val currentUser = auth.currentUser
    if (currentUser != null) {
        // L'utente è già autenticato, esegui la navigazione verso l'activity principale o il fragment desiderato
        val listGroupFragment = ListGroupFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, listGroupFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}

        }
    }*/


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()
        val registerButton = view.findViewById<Button>(R.id.registerClick)
        val emailEditText = view.findViewById<EditText>(R.id.email)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        registerButton.setOnClickListener {
            val registerFragment = RegisterFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, registerFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (!(email.isNotEmpty() && password.isNotEmpty())) {
                Toast.makeText(
                    requireContext(),
                    "Fill in the blank fields.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                progressBar.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        progressBar.visibility = View.GONE

                        if (task.isSuccessful) {
                            // Login riuscito
                            Toast.makeText(requireContext(), "Login successful.", Toast.LENGTH_LONG)
                                .show()
                            val listGroupFragment = ListGroupFragment()
                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, listGroupFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        } else {
                            // Login fallito
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Authentication Failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }
        }
        return view
    }
}

