package com.example.desafio_iii_mgi.Events

import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslCertificate
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
import java.lang.StringBuilder

class EventoActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var even:Evento

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
        txtBtnListaUsus.setOnClickListener{
            Toast.makeText(this, "cargar lista usus", Toast.LENGTH_SHORT).show()
        }

        createFragment()

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setOnMapLongClickListener(this)
    }

    private fun createMarker(coor: com.google.android.gms.maps.model.LatLng) {
        val marker: MarkerOptions = MarkerOptions().position(coor)
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 16f), 3000,null)
    }

    override fun onMapLongClick(coor: com.google.android.gms.maps.model.LatLng) {

        val objIntent: Intent = intent
        var ide: String? = objIntent.getStringExtra("id")

        if (ide != null) {
            db.collection("eventos").document(ide).get().addOnSuccessListener {
                var nombre: String? = it.get("nombre") as String?
                var fecha: String? = it.get("fecha") as String?
                var hora: String? = it.get("hora") as String?
                var lat: Double = it.get("lat") as Double
                var lon :Double = it.get("lon") as Double
                var listEve: ArrayList<String> = it.get("listEve") as ArrayList<String>
                var event = Evento(ide, nombre.toString(),fecha.toString(),hora.toString(),lat,lon, listEve)
                even = (event)
            }
        }



        createMarker(coor)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("¿Esta seguro de establecer esta ubicacion para el evento?")
        builder.setPositiveButton("Si",{ dialogInterface: DialogInterface, i: Int ->

            if (ide != null) {
                db.collection("eventos").document(ide).set(
                    hashMapOf(
                        "nombre" to even.nombre,
                        "fecha" to even.fecha,
                        "hora" to even.hora,
                        "lat" to coor.latitude,
                        "lon" to coor.longitude,
                        "listEve" to even.listEve
                    )
                )
            }

            finish()
            startActivity(getIntent())

        })

        builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int ->
            finish()
            startActivity(getIntent())
        })
        builder.show()
    }


}