package com.example.desafio_iii_mgi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.desafio_iii_mgi.Admin.AdminActivity
import com.example.desafio_iii_mgi.PrecargasApp.Companion.prefs
import com.example.desafio_iii_mgi.Users.User
import com.example.desafio_iii_mgi.Users.UserActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

private var GOOGLE_SIGN_IN = 1


class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        //ESCONDE LA BARRA DE TAREAS
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkUserValues()



        /*
         * ONCLICK DE LOS BOTONES
         */
        txtRegistro.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }

        btnWithGoogle.setOnClickListener {
            //Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id)) //Esto se encuentra en el archivo google-services.json: client->oauth_client -> client_id
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut() //Con esto salimos de la posible cuenta  de Google que se encuentre logueada.
            val signInIntent = googleClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)

        }

        btnLogin.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtContrasenia.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(), txtContrasenia.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {

                        db.collection("users").document(txtEmail.text.toString()).get().addOnSuccessListener {
                            var compAdm:Boolean = it.get("admin") as Boolean
                            var comp:Boolean = it.get("verificado") as Boolean

                            if (comp == true){
                                if (compAdm == true) {
                                    initUI(compAdm)
                                    val intent = Intent(this, AdminActivity::class.java)
                                    intent.putExtra("correo", txtEmail.text.toString())
                                    startActivity(intent)
                                } else {
                                    initUI(compAdm)
                                    val intent = Intent(this, UserActivity::class.java)
                                    intent.putExtra("correo", txtEmail.text.toString())
                                    startActivity(intent)
                                }
                            }else{
                                Toast.makeText(this, "Su cuenta todavia no ha sido activada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }

    override fun onBackPressed() {
        finishAffinity()
    }

    fun checkUserValues() {


        if (prefs.getRecor() == true) {

            if (prefs.getPwd() != "xxx"){
                if (prefs.getAdmin() == true) {
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.putExtra("correo", prefs.getCorreo())
                    startActivity(intent)
                } else {
                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("correo", prefs.getCorreo())
                    startActivity(intent)
                }

            }else{
                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra("correo", prefs.getCorreo())
                startActivity(intent)
            }

        }
    }


    private fun initUI(adm:Boolean) {

        prefs.saveCorreo(txtEmail.text.toString())
        prefs.savePwd(txtContrasenia.text.toString())
        prefs.saveRecordar(cbxSesion.isChecked)
        prefs.saveAdmin(adm)

    }

    private fun initUI_google(corro:String){
        prefs.saveCorreo(corro)
        prefs.savePwd("xxx")
        prefs.saveRecordar(true)
        prefs.saveAdmin(false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)!!

                if (account != null) {
                    val credential: AuthCredential= GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            var comp = true

                            db.collection("users").document(account.email.toString()).get().addOnSuccessListener {
                                try {
                                    var comp:Boolean = it.get("verificado") as Boolean

                                    if (comp){
                                        initUI_google(account.email.toString())
                                        val intent = Intent(this, UserActivity::class.java)
                                        intent.putExtra("correo", account.email.toString())
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(this, "Su cuenta todavia no ha sido activada", Toast.LENGTH_SHORT).show()
                                    }


                                }catch (e:Exception){
                                    Toast.makeText(this, "crear cuenta", Toast.LENGTH_SHORT).show()

                                    val intentRegister = Intent(this, RegisterActivity::class.java)
                                    intentRegister.putExtra("correo", account.email.toString())
                                    startActivity(intentRegister)

                                }
                            }

                        } else {
                        }
                    }
                }
            } catch (e: ApiException) {
            }
            
        }
    }



}