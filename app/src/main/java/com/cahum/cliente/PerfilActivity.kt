package com.cahum.cliente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Perfil"
        setContentView(R.layout.activity_perfil)
    }
}
