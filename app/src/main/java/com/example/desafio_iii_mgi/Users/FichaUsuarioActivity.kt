package com.example.desafio_iii_mgi.Users

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.desafio_iii_mgi.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ficha_usuario.*

class FichaUsuarioActivity : AppCompatActivity(), OnMapReadyCallback {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    private lateinit var  usurp: com.example.desafio_iii_mgi.Users.User
    private lateinit var nomb:String

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


        cargar_usuario(correo!!)
        createFragment()
    }


    private fun cargar_usuario(correo:String){
        db.collection("users").document(correo).get().addOnSuccessListener {


            var nom: String? = it.get("nombre") as String?
            var apellido: String? = it.get("apellidos") as String?
            var fecha:String?  = it.get("edad") as String?
            var verificado:Boolean? = it.get("verificado") as Boolean?
            var admin:Boolean? = it.get("admin")as Boolean?
            var listUsu:ArrayList<String> = it.get("listUsu") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
            var usu = User(correo, nom.toString(), apellido.toString(), fecha.toString(), verificado, admin,listUsu, 0.0,0.0)

            txtFichaEmail.text = usu.correo
            txtFichaNombre.text = usu.nombre
            txtFichaApe.text = usu.apellidos
            txtFichaFecha.text = usu.edad

        }
    }


    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
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
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION )
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

}