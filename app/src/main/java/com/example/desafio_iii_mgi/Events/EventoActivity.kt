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
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_evento.*
import kotlinx.android.synthetic.main.activity_ficha_usuario.*

class EventoActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_evento)

        // RECUPERO EL DATOS DEL ID
        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")



        if (id != null) {
            db.collection("eventos").document(id).get().addOnSuccessListener {
                txtNombreEve.text = it.get("nombre") as String?
                txtFechaEve.text = it.get("fecha") as String?
                txtHoraEve.text = it.get("hora") as String?

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
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLongClickListener(this)
    }

    private fun createMarker(coor: com.google.android.gms.maps.model.LatLng) {
        val marker: MarkerOptions = MarkerOptions().position(coor)
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 18f), 4000,null)
    }

    override fun onMapLongClick(coor: com.google.android.gms.maps.model.LatLng) {
        createMarker(coor)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("¿Esta seguro de establecer esta ubicacion para el evento?")
        builder.setPositiveButton("Si",{ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "si", Toast.LENGTH_SHORT).show()

        })

        builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
        })
        builder.show()
    }


}