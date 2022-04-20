package com.example.desafio_iii_mgi
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desafio_iii_mgi.Fragments.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_register.*


class
RegisterActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtEdad.setOnClickListener{
            showDatePickerDIalog()
        }

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
                                    "admin" to false
                                )
                            )
                            Toast.makeText(this, "Registro completo, un administrador le dara permiso en breve", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error el correo o la contraseña no es valida", Toast.LENGTH_SHORT).show()
                        }
                    }
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