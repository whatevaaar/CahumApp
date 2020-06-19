package com.cahum.cliente.control

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cahum.cliente.R
import com.cahum.cliente.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider


class LoginManager(contexto: Context) : Activity() {
    private val tag = "Login"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(contexto.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    private val mGoogleSignInClient = GoogleSignIn.getClient(contexto, gso)
    private val usuario = firebaseAuth.currentUser

    fun signIn() {
        if ( usuarioLogeado() )
            return
        val signInIntent: Intent = this.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task!!)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Toast.makeText(this, "Error al autenticarse", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(tag, "firebaseAuthWithGoogle:" + account.id!!)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun logout() = FirebaseAuth.getInstance().signOut()

    fun usuarioLogeado(): Boolean = usuario != null

}