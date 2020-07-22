package com.cahum.cliente

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        establecerPantallaCompleta()
        floatingBotonEnviar.setOnClickListener {
            startActivity(Intent(this,ChatLogActivity::class.java))
        }
        floatingBotonPerfil.setOnClickListener {
            startActivity(Intent(this,PerfilActivity::class.java))
        }
    }

    fun establecerPantallaCompleta() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        supportActionBar?.hide()
    }

}
