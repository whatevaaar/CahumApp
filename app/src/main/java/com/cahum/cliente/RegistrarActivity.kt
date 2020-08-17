package com.cahum.cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

const val RC_SIGN_IN = 200

class RegistrarActivity : AppCompatActivity() {
    private lateinit var botonSignIn: SignInButton
    private lateinit var botonRegistrar: Button
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Registro"
        botonSignIn = sign_in_button
        botonRegistrar = boton_registrar
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        botonSignIn.setOnClickListener {
            if (checkBoxTerminos.isChecked)
                signIn()
            else Toast.makeText(
                this,
                "Primero tienes que estar de acuerdo con los términos y condiciones.",
                Toast.LENGTH_LONG
            ).show()
        }
        botonRegistrar.setOnClickListener {
            if (checkBoxTerminos.isChecked)
                registrarConCorreo()
            else Toast.makeText(
                this,
                "Primero tienes que estar de acuerdo con los términos y condiciones.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun registrarUsuarioSiNoExiste(usuario: FirebaseUser) {

        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) ref.setValue(Cliente(usuario.uid, usuario.displayName!!))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun registrarConCorreo() {
        val nombre = texto_nombre.editText?.text.toString()
        val correo = texto_correo.editText?.text.toString()
        val pass = texto_password.editText?.text.toString()
        if (nombre.isEmpty() or correo.isEmpty() or pass.isEmpty()) {
            Toast.makeText(this, "Error al autenticarse", Toast.LENGTH_LONG).show()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                    registrarEnFirebase(nombre)
                    redirigirAMenu()
                }
            }
    }

    private fun registrarEnFirebase(nombre: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(nombre).build()
        val usuario = FirebaseAuth.getInstance().currentUser
        usuario!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    registrarUsuarioSiNoExiste(usuario)
                    Log.d("LOGIN", "User password updated.")
                }
            }
    }

    private fun signIn() {
        if (usuarioLogeado())
            return
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onStart() {
        super.onStart()
        if (usuarioLogeado()) {
            redirigirAMenu()
        }
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
        Log.d("LOGIN", "firebaseAuthWithGoogle:" + account.id!!)
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

    private fun redirigirAMenu() {
        MyFirebaseMessagingService().conseguirToken()
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun usuarioLogeado(): Boolean = FirebaseAuth.getInstance().currentUser != null

}
