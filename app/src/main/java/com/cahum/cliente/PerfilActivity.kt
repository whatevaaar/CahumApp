package com.cahum.cliente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cahum.cliente.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_perfil.*

class PerfilActivity : AppCompatActivity() {

    private val usuario = FirebaseAuth.getInstance().currentUser
    private val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario!!.uid}")
    private val tag = "Perfil"
    private var cliente: Cliente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Perfil"
        setContentView(R.layout.activity_perfil)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "ERROR")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cliente = dataSnapshot.getValue(Cliente::class.java)
                actualizarPantalla()
            }

        }
        ref.addValueEventListener(postListener)
    }
    private fun actualizarPantalla() {
        texto_nombre_perfil_cliente.text = usuario!!.displayName
        texto_correo_perfil_cliente.text = usuario.email
        supportActionBar?.title = "Perfil de ${cliente!!.nombre}"
        establecerCheckbox()
    }
    private fun establecerCheckbox() {
        checkBoxCv.isChecked = cliente!!.tieneCv
        checkBoxSimulador.isChecked = cliente!!.simulador
        checkBoxPsicometria.isChecked = cliente!!.psicometria
        checkBoxLinkedin.isChecked = cliente!!.perfilLinked
        checkBoxEstrategia.isChecked = cliente!!.estrategia
    }
}
