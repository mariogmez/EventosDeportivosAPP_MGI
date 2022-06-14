package com.example.desafio_iii_mgi
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.example.desafio_iii_mgi.Fragments.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*


class
RegisterActivity : AppCompatActivity() {

    var comp:Boolean = false
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtEdad.setOnClickListener{
            showDatePickerDIalog()
        }

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")
        Toast.makeText(this, ""+correo, Toast.LENGTH_SHORT).show()
        if (correo != null){
            txtCorreoRG.setText(correo.toString())
            txtCorreoRG.isEnabled = false
            editTextTextPassword.isEnabled = false
            comp = true
        }

        var lon:Double = 0.0
        var lat:Double = 0.0

        btnRegistrase.setOnClickListener {

            if (checkBox.isChecked){
                if (txtCorreoRG.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtCorreoRG.text.toString(),editTextTextPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            db.collection("users").document(txtCorreoRG.text.toString()).set(
                                hashMapOf(
                                    "nombre" to txtNombreRG.text.toString(),
                                    "apellidos" to txtApellidosRG.text.toString(),
                                    "edad" to txtEdad.text.toString(),
                                    "verificado" to false,
                                    "admin" to false,
                                    "listUsu" to arrayListOf(""),
                                    "lat" to lat,
                                    "lon" to lon
                                )
                            )
                            Toast.makeText(this, "Registro completo, un administrador le dara permiso en breve", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error el correo o la contraseña no es valida", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    if (comp){
                        db.collection("users").document(correo!!).set(
                            hashMapOf(
                                "nombre" to txtNombreRG.text.toString(),
                                "apellidos" to txtApellidosRG.text.toString(),
                                "edad" to txtEdad.text.toString(),
                                "verificado" to false,
                                "admin" to false,
                                "listUsu" to arrayListOf(""),
                                "lat" to lat,
                                "lon" to lon
                            )
                        )
                    }
                    Toast.makeText(this, "Registro completo, un administrador le dara permiso en breve", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "debe aceptar terminos y condiciones", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun showDatePickerDIalog() {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }
    fun onDateSelected(day:Int, month:Int, year:Int){
        txtEdad.setText("" +day+ "/" +month+ "/" +year)
    }


}