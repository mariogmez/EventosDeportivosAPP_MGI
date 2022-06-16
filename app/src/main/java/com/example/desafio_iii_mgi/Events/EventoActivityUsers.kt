package com.example.desafio_iii_mgi.Events

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.FichaUsuarioActivity
import com.example.desafio_iii_mgi.Users.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_evento_users.*

class EventoActivityUsers : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var even:Evento

    companion object{
        const val REQUEST_CODE_LOCATION = 1
    }

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
        map.setOnMyLocationChangeListener (this)
        enableLocation()

    }

    private fun createMarker(coor: com.google.android.gms.maps.model.LatLng) {
        val marker: MarkerOptions = MarkerOptions().position(coor)
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 16f), 3000,null)
    }


    /**
     * Aqui se encarga de enviar la ubicacion
     */

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
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                FichaUsuarioActivity.REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            FichaUsuarioActivity.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")
        var bol:Boolean? = objIntent.getStringExtra("bol").toBoolean()
        modificar_pos(correo!!, loc, bol!!)


    }

    private fun modificar_pos(correo:String, loc: Location, bol:Boolean){
        if (bol){
            db.collection("users").document(correo).get().addOnSuccessListener {


                var nom: String? = it.get("nombre") as String?
                var apellido: String? = it.get("apellidos") as String?
                var fecha:String?  = it.get("edad") as String?
                var verificado:Boolean? = it.get("verificado") as Boolean?
                var admin:Boolean? = it.get("admin")as Boolean?
                var listUsu:ArrayList<String> = it.get("listUsu") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                var usu = User(correo, nom.toString(), apellido.toString(), fecha.toString(), verificado, admin,listUsu, 0.0,0.0)


                set_location(usu, loc)
            }
        }

    }

    private fun set_location(usu: User, loc: Location){
        db.collection("users").document(usu.correo).set(
            hashMapOf(
                "nombre" to usu.nombre,
                "apellidos" to usu.apellidos,
                "edad" to usu.edad,
                "verificado" to usu.verificado,
                "admin" to usu.admin,
                "listUsu" to usu.listUsu,
                "lat" to loc.latitude,
                "lon" to loc.longitude
            )
        )
    }

}