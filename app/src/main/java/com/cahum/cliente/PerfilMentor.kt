package com.cahum.cliente

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.modelo.Cliente
import com.cahum.cliente.modelo.Mentor
import com.cahum.cliente.modelo.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_perfil_mentor.*

class PerfilMentor : AppCompatActivity() {
    private var mentor: Mentor? = null
    private var cliente: Cliente? = null
    private val tag = "PerfilMentor"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_mentor)
        mentor = intent.getParcelableExtra<Mentor>("USER_KEY")
        actualizarDatos()
        encontrarCliente()
        botonCalificar.setOnClickListener {
            subirCalificacion()
        }
    }

    private fun actualizarDatos() {
        val ref = FirebaseDatabase.getInstance().getReference("/mentores/${mentor!!.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("CHATLOG", "ERROR")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mentor = dataSnapshot.getValue(Mentor::class.java)
                    if (mentor != null) actualizarPantalla()
                }
            }
        }
        ref.addValueEventListener(postListener)
    }

    fun subirCalificacion() {
        val ref = FirebaseDatabase.getInstance().getReference("/calificaciones/${cliente!!.uidMentor}")
        ref.child(cliente!!.uid).setValue(Review(ratingBar.rating))
            .addOnSuccessListener {
                generarPromedio()
            }
    }

    private fun generarPromedio() {
        val ref = FirebaseDatabase.getInstance().getReference("/calificaciones/${cliente!!.uidMentor}")
        var calificaciones = 0
        var total = 0.0f
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                children.forEach {
                    val review = it.getValue(Review::class.java) ?: return
                    calificaciones += 1
                    total += review.calificacion
                }
                mentor!!.calificacion = (total / calificaciones).toDouble()
                actualizarMentor()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun actualizarMentor() {
        val ref = FirebaseDatabase.getInstance().getReference("/mentores")
        ref.child(mentor!!.uid).setValue(mentor)
            .addOnSuccessListener {
                Toast.makeText(this, "Calificado!", Toast.LENGTH_LONG).show()
            }
    }

    private fun encontrarCliente() {
        val usuario = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    cliente = dataSnapshot.getValue(Cliente::class.java)
                    if (cliente != null)
                        botonCalificar.isEnabled =
                            cliente!!.perfilLinked && cliente!!.psicometria && cliente!!.simulador && cliente!!.tieneCv && cliente!!.estrategia
                    ratingBar.setIsIndicator(!botonCalificar.isEnabled)
                }
            }
        }
        ref.addValueEventListener(postListener)
    }

    private fun actualizarPantalla() {
        texto_nombre_perfil_mentor.text = mentor!!.nombre
        supportActionBar?.title = "Perfil de ${mentor!!.nombre}"
        ratingBar.rating = mentor!!.calificacion.toFloat()
    }
}
