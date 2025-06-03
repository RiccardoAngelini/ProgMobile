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
import com.example.mobile.R
import com.example.mobile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cnfpasswordEditText: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = FirebaseAuth.getInstance()
        nameEditText = view.findViewById(R.id.name)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        progressBar = view.findViewById(R.id.progressBar)
        cnfpasswordEditText=view.findViewById(R.id.cnfpassword)

        val loginClick = view.findViewById<Button>(R.id.loginClick)
        loginClick.setOnClickListener {
            parentFragmentManager.popBackStack() // Torna al fragment precedente dalla back stack
        }

        val btnRegister = view.findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val cnfpassword = cnfpasswordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Fill in the blank fields.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!isEmailValid(email)) {
                Toast.makeText(
                    requireContext(),
                    "Invalid email format.",
                    Toast.LENGTH_LONG
                ).show()
            }  else if (!arePasswordsEqual(password, cnfpassword)) {
                Toast.makeText(
                    requireContext(),
                    "Passwords do not match.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Password must contain at least six characters.",
                    Toast.LENGTH_LONG
                ).show()
            }else {
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        progressBar.visibility = View.GONE /*rende la progress bar invisibile*/

                        if (task.isSuccessful) {
                            //salvataggio utente
                            val user = auth.currentUser
                            //modificare perche non funziona bene
                            if (user != null) {
                                val userId = user.uid
                                val newUser = User(userId, name, email, password)
                                val userCollection: CollectionReference =
                                    firestore.collection("user")
                                userCollection.document(userId).set(newUser).addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration success.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }.addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
            }
        }
        return view
    }


    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z][\\w.-]*@[A-Za-z]+\\.[A-Za-z]{2,}")
        return email.matches(emailRegex)
    }

    fun arePasswordsEqual(password1: String, password2: String): Boolean {
        return password1 == password2
    }
}