package com.example.desafio_iii_mgi.Events

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.desafio_iii_mgi.Admin.GestionUsuariosPorEvento
import com.example.desafio_iii_mgi.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_evento.*
import kotlinx.android.synthetic.main.activity_ficha_usuario.*
import kotlinx.android.synthetic.main.activity_main.*


class EventoActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var even:Evento
    private val CODE_GALLERY = 1

    val storage = Firebase.storage
    val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_evento)

        // RECUPERO EL DATOS DEL ID
        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")

        btnGaleria.setOnClickListener {
            mostrar_emergente()
        }

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
            val intent = Intent(this, GestionUsuariosPorEvento::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        }


        createFragment()

    }

    fun mostrar_emergente(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("¿Que desea hacer?")
        builder.setPositiveButton("Subir foto",{ dialogInterface: DialogInterface, i: Int ->

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                CODE_GALLERY
            )
        })

        builder.setNegativeButton("Consultar galeria",{ dialogInterface: DialogInterface, i: Int ->
        })
        builder.show()
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
                var activado:Boolean = it.get("activado") as Boolean
                var event = Evento(ide, nombre.toString(),fecha.toString(),hora.toString(),lat,lon, listEve, activado)
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
                        "listEve" to even.listEve,
                        "activado" to even.activado

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