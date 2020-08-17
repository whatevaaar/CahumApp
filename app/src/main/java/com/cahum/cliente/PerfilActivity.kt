package com.cahum.cliente

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cahum.cliente.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_perfil.*

class PerfilActivity : AppCompatActivity() {
    private val usuario = FirebaseAuth.getInstance().currentUser
    private val ref = FirebaseDatabase.getInstance().getReference("/clientes/${usuario!!.uid}")
    private var privateMode = 0
    private val temaBase = "mountain"
    private var temaActual = ""
    private val prefName = "tema"
    private val tag = "Perfil"
    private var cliente: Cliente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Perfil"
        setContentView(R.layout.activity_perfil)
        cargarAjustes()
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
        //Listeners
        imgFlechaDer.setOnClickListener {
            when (temaActual) {
                "mountain" -> {
                    temaActual = "cactus"
                    cambiarImgPlatform()
                }
                "cactus" -> {
                    temaActual = "sky"
                    cambiarImgPlatform()
                }
                "sky" -> {
                    temaActual = "sea"
                    cambiarImgPlatform()
                }
                "sea" -> {
                    temaActual = "mountain"
                    cambiarImgPlatform()
                }
            }
        }

        imgFlechaIzq.setOnClickListener {
            when (temaActual) {
                "mountain" -> {
                    temaActual = "sea"
                    cambiarImgPlatform()
                }
                "sea" -> {
                    temaActual = "sky"
                    cambiarImgPlatform()
                }
                "sky" -> {
                    temaActual = "cactus"
                    cambiarImgPlatform()
                }
                "cactus" -> {
                    temaActual = "mountain"
                    cambiarImgPlatform()
                }
            }
        }
    }

    private fun cambiarImgPlatform() {
        val sharedPref: SharedPreferences = getSharedPreferences(prefName, privateMode)
        val id: Int = encontrarIdImg("platform_${temaActual}")
        imagePlatform.setImageResource(id)
        val editor = sharedPref.edit()
        editor.clear()
        editor.putString(prefName, temaActual)
        editor.commit()
    }

    private fun encontrarIdImg(nombreImg: String): Int = resources.getIdentifier(nombreImg, "drawable", packageName)
    private fun cargarAjustes() {
        val sharedPref: SharedPreferences = getSharedPreferences(prefName, privateMode)
        temaActual = sharedPref.getString(prefName, temaBase) ?: return
        cambiarImgPlatform()
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

    //Código de Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, InicioActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
