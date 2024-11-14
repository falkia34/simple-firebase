package dev.rulim34.simplefirebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(requireContext())

        // Initialize views
        emailInput = view.findViewById(R.id.etEmail)
        passwordInput = view.findViewById(R.id.etPassword)

        // Setup click listeners
        view.findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            signIn()
        }

        view.findViewById<TextView>(R.id.tvGoToSignUp).setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_signUpFragment)
        }

        view.findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    private fun signIn() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_authFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setAutoSelectEnabled(false)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.google_web_client_id))
                .build()

            val signInRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val response = credentialManager.getCredential(requireContext(), signInRequest)
                val credential = response.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val authCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    auth.signInWithCredential(authCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                findNavController().navigate(R.id.action_authFragment_to_homeFragment)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}