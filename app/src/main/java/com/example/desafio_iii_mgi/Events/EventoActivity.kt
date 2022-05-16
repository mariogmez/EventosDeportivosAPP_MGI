package com.example.desafio_iii_mgi.Events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desafio_iii_mgi.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_evento.*
import kotlinx.android.synthetic.main.activity_ficha_usuario.*

class EventoActivity : AppCompatActivity(), OnMapReadyCallback {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evento)

        // RECUPERO EL DATOS DEL ID
        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")

        if (id != null) {
            db.collection("eventos").document(id).get().addOnSuccessListener {
                txtNombreEve.text = it.get("nombre") as String?
                txtFechaEve.text = it.get("fecha") as String?
                txtHoraEve.text = it.get("hora") as String?
                txtUbiEve.text = it.get("ubicacion") as String?
            }
        }

        createFragment()


    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    private fun createMarker() {
        //val coordinates = LatLng()
    }
}