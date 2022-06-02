package com.example.desafio_iii_mgi.Events

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.desafio_iii_mgi.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_evento_users.*

class EventoActivityUsers : AppCompatActivity(), OnMapReadyCallback {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var even:Evento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_evento_users)

        // RECUPERO EL DATOS DEL ID
        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")


        if (id != null) {
            db.collection("eventos").document(id).get().addOnSuccessListener {
                txtNombreEveUsu.text = it.get("nombre") as String?
                txtFechaEveUsu.text = it.get("fecha") as String?
                txtHoraEveUsu.text = it.get("hora") as String?


                if ((it.get("lat") as Double) != 0.0 && (it.get("lon") as Double) != 0.0){
                    createMarker(com.google.android.gms.maps.model.LatLng(it.get("lat") as Double, it.get("lon") as Double))
                }else{
                    Toast.makeText(this, "Ubicacion del evento no definida", Toast.LENGTH_SHORT).show()
                }

            }
        }



        createFragment()

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map4) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

    }

    private fun createMarker(coor: com.google.android.gms.maps.model.LatLng) {
        val marker: MarkerOptions = MarkerOptions().position(coor)
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 16f), 3000,null)
    }

}