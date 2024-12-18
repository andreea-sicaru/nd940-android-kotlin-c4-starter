package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthenticationViewModel>()
    private lateinit var binding: ActivityAuthenticationBinding

    companion object {
        const val TAG = "🦄AuthenticationActivity"
    }

    private var activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val response = IdpResponse.fromResultIntent(data)
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user: ${FirebaseAuth.getInstance().currentUser?.displayName}"
                )
                finish()
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Implement the create account and sign in using FirebaseUI,
        //  use sign in using email and sign in using Google
        binding.loginButton.setOnClickListener { launchSignInFlow() }

        // TODO: If the user was authenticated, send him to RemindersActivity
        observeAuthenticationState()

        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        activityResult.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
        )
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    startReminderActivity()
                    finish()
                }
                else -> {
                    Log.i(TAG, "Sign in unsuccessful $authenticationState")
                }
            }

        })
    }

    private fun startReminderActivity() {
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }
}