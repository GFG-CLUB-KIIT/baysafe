package com.gfg.kiit.baysafe.feature

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gfg.kiit.baysafe.R
import com.gfg.kiit.baysafe.data.SavedPreference
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
        if(!checkForInternet())
        {
            askToEnableInternet()
        }

        if(GoogleSignIn.getLastSignedInAccount(requireContext())!=null)
            {
                findNavController().navigate(R.id.action_signUpFragment_to_permissionFragment)
            }
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signUpBtn.setOnClickListener {
            binding.loginProgressbar.visibility = View.VISIBLE
            val userName = binding.loginNameEditText.text.toString().trim()
            val userEmail = binding.loginPhoneEditText.text.toString().trim()
            val userPassword = binding.loginPasswordEditText.text.toString().trim()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && userName.isNotEmpty()) {
                binding.loginProgressbar.visibility = View.VISIBLE
                createUserWithEmailAndPassword(userEmail, userPassword,userName)
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

    override fun onResume() {
        super.onResume()
        if(!checkForInternet())
        {
            askToEnableInternet()
        }
        binding.loginProgressbar.visibility = View.GONE
    }

    private fun checkForInternet(): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun askToEnableInternet() {
        AlertDialog.Builder(requireContext()).setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _,_ ->
                val intent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            .setIcon(android.R.drawable.ic_dialog_alert).show()
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

    private fun createUserWithEmailAndPassword(email: String, password: String,username:String) {
        activity?.let {
            auth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    it
                ) { task ->
                    val message: String
                    if (task.isSuccessful) {
                        message = "success createUserWithEmailAndPassword"
                        SavedPreference.setEmail(requireContext(),email)
                        SavedPreference.setName(requireContext(),username)
                        findNavController().navigate(R.id.action_signUpFragment_to_permissionFragment)
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
            .requestIdToken("854789459236-js3mmca0pmoe1a49m4nm7v40cliv5kii.apps.googleusercontent.com")
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
                    SavedPreference.setEmail(requireContext(),acct.email!!)
                    SavedPreference.setName(requireContext(),acct.displayName!!)
                    findNavController().navigate(R.id.action_signUpFragment_to_permissionFragment)
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



}
