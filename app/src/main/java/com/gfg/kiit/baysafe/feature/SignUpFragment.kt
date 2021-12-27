package com.gfg.kiit.baysafe.feature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.gfg.kiit.baysafe.R
import com.gfg.kiit.baysafe.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var mGoogleApiClient: GoogleSignInClient
    private lateinit var mAuthStateListener: AuthStateListener
    private val rcSignIn: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        initFBGoogleSignIn()
        initFBAuthState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //close app
                    requireActivity().finishAffinity()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(mAuthStateListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signUpBtn.setOnClickListener {
            binding.loginProgressbar.visibility = View.VISIBLE
            val userName = binding.loginNameEditText.text.toString().trim()
            val userEmail = binding.loginPhoneEditText.text.toString().trim()
            val userPassword = binding.loginPasswordEditText.text.toString().trim()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && userName.isNotEmpty()) {
                binding.loginProgressbar.visibility = View.VISIBLE
                createUserWithEmailAndPassword(userEmail, userPassword)
            } else {
                binding.loginProgressbar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "Please fill up the Credentials :|",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.singInTv.setOnClickListener {
            view.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.backBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.signUpGoolge.setOnClickListener {
            signInWithGoogleSignIn()
        }
    }

    private fun initFBAuthState() {
        mAuthStateListener = AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            val message: String = if (firebaseUser != null) {
                "onAuthStateChanged signed in : " + firebaseUser.uid
            } else {
                "onAuthStateChanged signed out"
            }
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        activity?.let {
            auth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    it
                ) { task ->
                    val message: String
                    if (task.isSuccessful) {
                        message = "success createUserWithEmailAndPassword"
                        view?.findNavController()
                            ?.navigate(R.id.action_signUpFragment_to_permissionFragment)
                    } else {
                        message = "fail createUserWithEmailAndPassword"
                    }
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
        }
    }

    private fun initFBGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()
        val context: Context = requireContext()
        mGoogleApiClient = GoogleSignIn.getClient(context, gso)
    }

    private fun signInWithGoogleSignIn() {
        val signInIntent = mGoogleApiClient.signInIntent
        requireActivity().startActivityFromFragment(this, signInIntent, rcSignIn)
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        activity?.let {
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener(
                    it
                ) { task ->
                    val message: String = if (task.isSuccessful) {
                        "success firebaseAuthWithGoogle"
                    } else {
                        "fail firebaseAuthWithGoogle"
                    }
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                    view?.findNavController()
                        ?.navigate(R.id.action_signUpFragment_to_permissionFragment)
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                Toast.makeText(
                    context,
                    "Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.loginProgressbar.visibility = View.GONE
    }
}
