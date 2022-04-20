package com.example.desafio_iii_mgi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.desafio_iii_mgi.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ficha_usuario.*

class FichaUsuarioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_usuario)
        supportActionBar?.title = "Ficha del usuario "
        // RECUPERO EL DATOS DEL CORREO
        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")



        if (correo != null) {
            db.collection("users").document(correo).get().addOnSuccessListener {
                txtFichaEmail.text = correo
                txtFichaNombre.text = it.get("nombre") as String?
                txtFichaApe.text = it.get("apellidos") as String?
                txtFichaFecha.text = it.get("edad") as String?
                if (it.get("verificado") == true){
                    txtFichaActiv.text = "activado"
                }else{
                    txtFichaActiv.text = "desactivado"
                }

            }
        }
    }
}