package com.example.desafio_iii_mgi.Admin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ficha_usuario.*
import kotlinx.android.synthetic.main.activity_localizacion_usuarios.*

class LocalizacionUsuariosActivity : AppCompatActivity(), OnMapReadyCallback {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var  cor:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localizacion_usuarios)
        supportActionBar?.hide()

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")

        createFragment()
        cor = (correo.toString())

        btnReload.setOnClickListener {
            finish();
            startActivity(getIntent());
        }
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map3) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        createMarker()
    }

    private fun createMarker() {

        if (cor != null) {
            db.collection("users").document(cor).get().addOnSuccessListener {


                var nom: String? = it.get("nombre") as String?
                var apellido: String? = it.get("apellidos") as String?
                var fecha:String?  = it.get("edad") as String?
                var verificado:Boolean? = it.get("verificado") as Boolean?
                var admin:Boolean? = it.get("admin")as Boolean?
                var listUsu:ArrayList<String> = it.get("listUsu") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                var lat:Double = it.get("lat") as Double
                var lon:Double = it.get("lon") as Double
                var usu = User(cor, nom.toString(), apellido.toString(), fecha.toString(), verificado, admin,listUsu, lat,lon)

                val coor =LatLng(usu.lat, usu.lon)
                val marker: MarkerOptions = MarkerOptions().position(coor)
                map.addMarker(marker)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 16f), 3000,null)

            }
        }

    }

}