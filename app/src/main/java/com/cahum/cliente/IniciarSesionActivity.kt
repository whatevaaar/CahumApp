package com.cahum.cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.control.MyFirebaseMessagingService
import com.cahum.cliente.modelo.Cliente
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_iniciar_sesion.*
import kotlinx.android.synthetic.main.activity_main.sign_in_button

class IniciarSesionActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var botonSignIn: SignInButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Iniciar Sesión"
        setContentView(R.layout.activity_iniciar_sesion)
        botonSignIn = sign_in_button
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        botonSignIn.setOnClickListener {
            signIn()
        }
        boton_iniciar.setOnClickListener {
            signInCorreo()
        }
    }

    private fun signInCorreo() {
        val correo = texto_correo_signin.editText?.text.toString()
        val pass = texto_password_signin.editText?.text.toString()
        if (correo.isEmpty() or pass.isEmpty()) {
            Toast.makeText(this, "Error al autenticarse", Toast.LENGTH_LONG).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                redirigirAMenu()
            } else {
                Toast.makeText(this, "Credenciales Incorrectas", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
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
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                    registrarUsuarioSiNoExiste(FirebaseAuth.getInstance().currentUser!!)
                    redirigirAMenu()
                } else Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
    }

    fun registrarUsuarioSiNoExiste(usuario: FirebaseUser) {

        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) ref.setValue(Cliente(usuario.uid,usuario.displayName!!))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }
    private fun redirigirAMenu() {
        MyFirebaseMessagingService().conseguirToken()
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
