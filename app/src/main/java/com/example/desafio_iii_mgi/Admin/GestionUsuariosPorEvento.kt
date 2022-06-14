package com.example.desafio_iii_mgi.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorRVusus
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorUsuariosEnEvento
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class GestionUsuariosPorEvento : AppCompatActivity() {
    private val db = Firebase.firestore // <- Inicializamos la BBDD

    /*
     * DECLARAMOS LA VARIBLES NECESARIAS PARA UTILIZAR
     * RECYCLERVIEW @MiApadatorRVusus
     */
    lateinit var mRecyclerViewUsu: RecyclerView
    var miArrayUsu:ArrayList<User> = ArrayList()
    val mAdapterUsu : MiAdaptadorUsuariosEnEvento = MiAdaptadorUsuariosEnEvento()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios_por_evento)
        supportActionBar?.hide()

        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")


    }




}