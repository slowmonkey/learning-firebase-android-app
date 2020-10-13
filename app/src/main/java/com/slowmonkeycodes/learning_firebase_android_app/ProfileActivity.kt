package com.slowmonkeycodes.learning_firebase_android_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions

    private lateinit var logoutButton: Button
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userId: TextView
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        logoutButton = findViewById(R.id.logoutButton)
        userName = findViewById(R.id.name)
        userEmail = findViewById(R.id.email)
        userId = findViewById(R.id.userId)
        profileImage = findViewById(R.id.profileImage)

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            googleSignInClient.signOut()

            Auth.GoogleSignInApi.signOut(googleSignInClient.asGoogleApiClient()).setResultCallback { status ->
                if (status.isSuccess) {
                    gotoMainActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Session is not closed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val googleSignInAccountTask = googleSignInClient.silentSignIn()

        if (googleSignInAccountTask.isSuccessful) {
            handleSignInResult(googleSignInAccountTask.result)
        }
        else {
            googleSignInAccountTask.addOnCompleteListener { task ->
                try {
                    val googleSignInAccount = task.getResult(ApiException::class.java)
                    handleSignInResult(googleSignInAccount)
                } catch (apiException: ApiException) {
                    Toast.makeText(
                        this,
                        "Error: ${apiException.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handleSignInResult(googleSignInAccount: GoogleSignInAccount?) {
        userName.text = googleSignInAccount!!.displayName
        userEmail.text = googleSignInAccount.email
        userId.text = googleSignInAccount.id

        try {
            Glide.with(this).load(googleSignInAccount.photoUrl).into(profileImage)
        } catch (exception: NullPointerException) {
            Toast.makeText(
                this,
                "Profile Image Not Found",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}