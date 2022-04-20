package com.example.desafio_iii_mgi.Users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.desafio_iii_mgi.R
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // RECUPERO EL DATOS DEL CORREO
        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")

        if (correo != null) {
            db.collection("users").document(correo).get().addOnSuccessListener {
                supportActionBar?.title = "Bienvenido " + it.get("nombre") as String?

            }
        }
    }
}