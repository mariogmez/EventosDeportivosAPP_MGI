package com.example.desafio_iii_mgi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.desafio_iii_mgi.Admin.AdminActivity
import com.example.desafio_iii_mgi.Users.UserActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

private var RC_SIGN_IN = 1
class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        //ESCONDE LA BARRA DE TAREAS OASDFASDF
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        txtRegistro.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }

        btnWithGoogle.setOnClickListener {
            //Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token)) //Esto se encuentra en el archivo google-services.json: client->oauth_client -> client_id
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this,googleConf) //Este será el cliente de autenticación de Google.
            googleClient.signOut() //Con esto salimos de la posible cuenta  de Google que se encuentre logueada.
            val signInIntent = googleClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btnLogin.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtContrasenia.text.isNotEmpty()){
                db.collection("users").document(txtEmail.text.toString()).get().addOnSuccessListener {
                    var compAdm:Boolean = false
                    compAdm = it.get("admin") == true
                    if (it.get("verificado") == true){
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(),txtContrasenia.text.toString()).addOnCompleteListener {
                            if (it.isSuccessful){
                                if (compAdm == true){
                                    val intent = Intent(this, AdminActivity::class.java)
                                    intent.putExtra("correo", txtEmail.text.toString())
                                    startActivity(intent)
                                }else{
                                    val intent = Intent(this, UserActivity::class.java)
                                    intent.putExtra("correo", txtEmail.text.toString())
                                    startActivity(intent)
                                }

                            } else {
                                Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this, "Su cuenta todavia no ha sido activada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        session()
        
    }

    private fun session(){
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE) //Aquí no invocamos al edit, es solo para comprobar si tenemos datos en sesión.
        val email:String? = prefs.getString("email",null)
        if (email != null){
            db.collection("users").document(email).get().addOnSuccessListener {
                var compAdm:Boolean = false
                compAdm = it.get("admin") == true
                if (it.get("verificado") == true){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(),txtContrasenia.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            if (compAdm == true){
                                val intent = Intent(this, AdminActivity::class.java)
                                intent.putExtra("correo", txtEmail.text.toString())
                                startActivity(intent)
                            }else{
                                val intent = Intent(this, UserActivity::class.java)
                                intent.putExtra("correo", txtEmail.text.toString())
                                startActivity(intent)
                            }

                        } else {
                            Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Su cuenta todavia no ha sido activada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Si la respuesta de esta activity se corresponde con la inicializada es que viene de la autenticación de Google.
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Fernando", "firebaseAuthWithGoogle:" + account.id)
                //Ya tenemos la id de la cuenta. Ahora nos autenticamos con FireBase.
                if (account != null) {
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Entro con google", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                //firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }
}