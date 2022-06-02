package com.example.desafio_iii_mgi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ficha_usuario.*
import java.util.jar.Manifest

class FichaUsuarioActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var  usu:User

    companion object{
        const val REQUEST_CODE_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_usuario)
        supportActionBar?.hide()
        // RECUPERO EL DATOS DEL CORREO
        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")

        if (correo != null) {
            db.collection("users").document(correo).get().addOnSuccessListener {


                var nom: String? = it.get("nombre") as String?
                var apellido: String? = it.get("apellidos") as String?
                var fecha:String?  = it.get("edad") as String?

                txtFichaEmail.text = correo
                txtFichaNombre.text = nom
                txtFichaApe.text = apellido
                txtFichaFecha.text = fecha
            }
        }

        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setOnMyLocationChangeListener (this)
        enableLocation()

    }


    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun enableLocation(){
        if (!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Acepta los permisos de localizacion", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE_LOCATION )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Acepta los permisos de localizacion", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Acepta los permisos de localizacion", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationChange(loc: Location) {
        var ln = loc.longitude
        var lt = loc.latitude

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")


        Toast.makeText(this, "Coordenas, lat->" + lt + ", lon->" +ln, Toast.LENGTH_SHORT).show()

    }
}