package com.cahum.cliente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.modelo.Mentor
import kotlinx.android.synthetic.main.activity_perfil_mentor.*

class PerfilMentor : AppCompatActivity() {
    private var mentor: Mentor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_mentor)
        mentor = intent.getParcelableExtra<Mentor>("USER_KEY")
        actualizarPantalla()
    }

    private fun actualizarPantalla() {
        texto_nombre_perfil_mentor.text = mentor!!.nombre
        supportActionBar?.title = "Perfil de ${mentor!!.nombre}"
        ratingBar.rating = mentor!!.calificacion.toFloat()
    }
}
