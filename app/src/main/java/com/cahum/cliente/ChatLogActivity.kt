package com.cahum.cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.modelo.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatlog.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var usuarioDestino: Mentor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        encontrarMentor()
        recyclerview_chat.adapter = adapter
        boton_enviar.setOnClickListener {
            mandarMensaje()
        }
    }

    private fun encontrarMentor() {
        val usuario = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("CHATLOG", "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val cliente = dataSnapshot.getValue(Cliente::class.java)
                    if (cliente != null) {
                        if (cliente.uidMentor.isEmpty()) return
                        else regresarMentor(cliente.uidMentor)
                    }
                }
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun regresarMentor(uidMentor: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/mentores/${uidMentor}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("CHATLOG", "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val mentor = dataSnapshot.getValue(Mentor::class.java)
                    if (mentor != null) {
                        usuarioDestino = mentor
                        supportActionBar?.title = usuarioDestino!!.nombre
                        escucharMensajes()
                    }
                }
            }
        }
        ref.addListenerForSingleValueEvent(postListener)

    }

    private fun escucharMensajes() {
            val usuario = FirebaseAuth.getInstance().uid
            val ref =
                FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${usuario}/${usuarioDestino!!.uid}")
            ref.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val mensaje = p0.getValue(Mensaje::class.java) ?: return
                    if (mensaje.fromId == usuario)
                        adapter.add(ChatItemFrom(mensaje.texto))
                    else
                        adapter.add(ChatItemTo(mensaje.texto))
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })

        recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
        }

        private fun mandarMensaje() {
            val texto = texto_mensaje.text.toString()
            val fromId = FirebaseAuth.getInstance().uid ?: return
            val toId = usuarioDestino!!.uid
            val refMandar = FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${fromId}/${toId}").push()
            val refRecibir = FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${toId}/${fromId}").push()
            val refUltimoMensaje = FirebaseDatabase.getInstance().getReference("/ultimos-mensajes/${fromId}/${toId}")
            val refUltimoMensajeMandar = FirebaseDatabase.getInstance().getReference("/ultimos-mensajes/${toId}/${fromId}")
            val mensaje = Mensaje(refMandar.key!!, texto, fromId, toId, System.currentTimeMillis() / 1000)
            refRecibir.setValue(mensaje)
            refUltimoMensaje.setValue(mensaje)
            refUltimoMensajeMandar.setValue(mensaje)
            refMandar.setValue(mensaje)
                .addOnSuccessListener {
                    texto_mensaje.text.clear()
                    recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
                }
        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatlog_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_perfil_usuario -> {
                if (usuarioDestino == null) Toast.makeText(this,"Todavía no tienes mentor!",Toast.LENGTH_LONG).show()
                else llamarActividad()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun llamarActividad(){

        val intent = Intent(this, PerfilMentor::class.java)
        intent.putExtra("USER_KEY", usuarioDestino)
        startActivity(intent)
    }
}
