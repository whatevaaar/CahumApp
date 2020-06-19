package com.cahum.cliente.control

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DBManager{
    private val db = Firebase.firestore
    private val tag = "DBManager"
    private val usuarios = db.collection("clientes")

    fun registrarUsuarioSiNoExiste(usuario: FirebaseUser){
        val docRef = usuarios.document(usuario.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document == null) { //No existe el usuario en la db
                    registrarUsuario(usuario)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(tag, "get failed with ", exception)
            }
    }

    private fun registrarUsuario(usuario: FirebaseUser) {
        val nuevoUsario = hashMapOf(
            "id" to usuario.uid,
            "nombre" to usuario.displayName,
            "cv" to false,
            "perfilLinkedIn" to false,
            "simulador" to false,
            "psicometria" to false,
            "estrategias" to false
        )
        usuarios.document(usuario.uid).set(nuevoUsario)
            .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing document", e) }
    }

}