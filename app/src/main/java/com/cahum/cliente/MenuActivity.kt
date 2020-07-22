package com.cahum.cliente

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.cahum.cliente.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    private val tag = "MenuActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        establecerPantallaCompleta()
        encontrarCliente()
        floatingBotonEnviar.setOnClickListener {
            startActivity(Intent(this,ChatLogActivity::class.java))
        }
        floatingBotonPerfil.setOnClickListener {
            startActivity(Intent(this,PerfilActivity::class.java))
        }
    }

    private fun encontrarCliente() {
        val usuario = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d(tag, "ERROR") }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    desaparecerImagenesDePersonaje()
                    val cliente = dataSnapshot.getValue(Cliente::class.java)
                    if (cliente != null) establecerPersonaje(cliente)
                }
            }
        }
        ref.addValueEventListener(postListener)
    }
    private fun establecerPersonaje(cliente: Cliente) {
        val progreso: Int = calcularProgreso(cliente)
        when(progreso){
            0 -> imgPersonaje0.visibility = View.VISIBLE
            1 -> imgPersonaje1.visibility = View.VISIBLE
            2 -> imgPersonaje2.visibility = View.VISIBLE
            3 -> imgPersonaje3.visibility = View.VISIBLE
            4 -> imgPersonaje4.visibility = View.VISIBLE
            5 -> imgPersonaje5.visibility = View.VISIBLE
            else -> imgPersonaje0.visibility = View.VISIBLE
        }

    }

    private fun desaparecerImagenesDePersonaje() {
        imgPersonaje0.visibility = View.GONE
        imgPersonaje1.visibility = View.GONE
        imgPersonaje2.visibility = View.GONE
        imgPersonaje3.visibility = View.GONE
        imgPersonaje4.visibility = View.GONE
        imgPersonaje5.visibility = View.GONE
    }

    private fun calcularProgreso(cliente: Cliente): Int {
        var total = 0
        if (cliente.estrategia) total += 1
        if (cliente.tieneCv) total += 1
        if (cliente.simulador) total += 1
        if (cliente.psicometria) total += 1
        if (cliente.perfilLinked) total += 1
        return total
    }


    fun establecerPantallaCompleta() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        supportActionBar?.hide()
    }

}
