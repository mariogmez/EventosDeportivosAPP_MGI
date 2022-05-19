package com.example.desafio_iii_mgi.Admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.example.desafio_iii_mgi.FichaUsuarioActivity
import com.example.desafio_iii_mgi.PrecargasApp.Companion.prefs
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class AdminActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        //supportActionBar?.hide()

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")
        var img: ImageView = findViewById(R.id.imgQR)


        if (correo != null) {
            db.collection("users").document(correo).get().addOnSuccessListener {
                supportActionBar?.title = "Bienvenido " + it.get("nombre") as String?

            }

            try {
                var  barEncoder = BarcodeEncoder()
                var bitmap = barEncoder.encodeBitmap(correo, BarcodeFormat.QR_CODE, 750,750)
                img.setImageBitmap(bitmap)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        btnGestUsuarios.setOnClickListener {
            val intent = Intent(this, ListadoUsuariosActivity::class.java)
            startActivity(intent)
        }

        btnGestEventos.setOnClickListener {

            val intent = Intent(this, LIstadoEventosActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnMenuCerrarSesion -> {
                Toast.makeText(this, "cerrando sesion", Toast.LENGTH_SHORT).show()
                prefs.wipe()
                FirebaseAuth.getInstance().signOut()
                onBackPressed()
            }

            R.id.btnMenuEscanearQR -> {
                initScanner()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //QR
    private fun initScanner() {
        IntentIntegrator(this).initiateScan()
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null){
            if (result.contents == null){
                Toast.makeText(this, "cancelado", Toast.LENGTH_SHORT).show()

            }else{

                val intent = Intent(this, FichaUsuarioActivity::class.java)
                intent.putExtra("correo", result.contents)
                startActivity(intent)

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}