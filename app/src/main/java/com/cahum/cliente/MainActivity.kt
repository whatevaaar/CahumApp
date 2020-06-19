package com.cahum.cliente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.control.DBManager
import com.cahum.cliente.control.LoginManager
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.cahum.cliente.Menu as Menu

const val RC_SIGN_IN = 200

class MainActivity : AppCompatActivity() {
    private lateinit var botonSignIn:SignInButton
    private lateinit var loginManager: LoginManager
    private val dbManager= DBManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginManager = LoginManager(this)
        botonSignIn = findViewById(R.id.sign_in_button)
        botonSignIn.setOnClickListener{
            loginManager.signIn()
        }
    }


    override fun onStart() {
        super.onStart()
        if (loginManager.usuarioLogeado()) {
            val usuario = FirebaseAuth.getInstance().currentUser
            dbManager.registrarUsuarioSiNoExiste(usuario!!)
            val intent = Intent(this,Menu::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}
