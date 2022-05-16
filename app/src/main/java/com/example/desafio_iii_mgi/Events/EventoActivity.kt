package com.example.desafio_iii_mgi.Events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desafio_iii_mgi.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_evento.*
import kotlinx.android.synthetic.main.activity_ficha_usuario.*

class EventoActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evento)

        // RECUPERO EL DATOS DEL ID
        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

        if (id != null) {
            db.collection("eventos").document(id).get().addOnSuccessListener {
                txtNombreEve.text = it.get("nombre") as String?
                txtFechaEve.text = it.get("fecha") as String?
                txtHoraEve.text = it.get("hora") as String?
                txtUbiEve.text = it.get("ubicacion") as String?
            }
        }


    }
}