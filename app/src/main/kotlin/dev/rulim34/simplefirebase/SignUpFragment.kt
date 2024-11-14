package dev.rulim34.simplefirebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        auth = FirebaseAuth.getInstance()

        emailInput = view.findViewById(R.id.etEmail)
        passwordInput = view.findViewById(R.id.etPassword)
        confirmPasswordInput = view.findViewById(R.id.etConfirmPassword)

        view.findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            signUp()
        }

        view.findViewById<TextView>(R.id.tvBackToSignIn).setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    private fun signUp() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if (!validateInput(email, password, confirmPassword)) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Sign up successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                } else {
                    handleSignUpError(task.exception)
                }
            }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            emailInput.error = "Email cannot be empty"
            emailInput.requestFocus()
            return false
        }

        if (!isValidEmail(email)) {
            emailInput.error = "Invalid email format"
            emailInput.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password cannot be empty"
            passwordInput.requestFocus()
            return false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error = "Please confirm your password"
            confirmPasswordInput.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            confirmPasswordInput.requestFocus()
            return false
        }

        if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            passwordInput.requestFocus()
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use a stronger password."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format. Please check your email."
            is FirebaseAuthUserCollisionException -> "This email is already in use. Please use a different email."
            else -> "Sign up failed: ${exception?.message ?: "Unknown error"}"
        }

        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}