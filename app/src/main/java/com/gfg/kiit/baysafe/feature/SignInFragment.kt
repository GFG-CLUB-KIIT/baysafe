package com.gfg.kiit.baysafe.feature

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.gfg.kiit.baysafe.R
import com.gfg.kiit.baysafe.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var mGoogleApiClient: GoogleSignInClient
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    companion object {
        // Arbitrary INT value
        const val rcSignIn = 123
    }

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
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpTv.setOnClickListener {
            view.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.signInBtn.setOnClickListener {
            binding.loginProgressbar.visibility = View.VISIBLE
            val email = binding.loginNameEditText.text.toString()
            val password = binding.loginPasswordEditText.text.toString()


            if (!TextUtils.isEmpty(email)) {
                binding.loginProgressbar.visibility = View.VISIBLE
                loginUser(email, password)
                binding.loginProgressbar.visibility = View.GONE
            }
        }

        binding.signInUsingGoogle.setOnClickListener {
            signInWithGoogleSignIn()
        }
    }

    /*
    * Methods required to Sign In using Google
    */
    private fun initFBAuthState() {
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
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


    /*
    * LOGIN USiNG EMAIL ID
    */
    private fun loginUser(mail: String, password: String) {
        if (!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(password)) {
            auth!!.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show()
                        view?.findNavController()
                            ?.navigate(R.id.action_signUpFragment_to_permissionFragment)

                    } else {
                        binding.loginProgressbar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Invalid credentials, Try Again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    /*
    * Get Result from Google Sign In Intent
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                val account = result?.signInAccount
                firebaseAuthWithGoogle(account!!)
                view?.findNavController()
                    ?.navigate(R.id.action_signUpFragment_to_permissionFragment)
            } else {
                Toast.makeText(
                    context,
                    "Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}