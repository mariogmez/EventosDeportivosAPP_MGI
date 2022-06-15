package com.example.desafio_iii_mgi.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorRVusus
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorUsuariosEnEvento
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_gestion_usuarios_por_evento.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class GestionUsuariosPorEvento : AppCompatActivity() {

    private val db = Firebase.firestore // <- Inicializamos la BBDD
    lateinit var mRecyclerViewUsu: RecyclerView
    var miArrayUsu:ArrayList<String> = arrayListOf()
    val mAdapterUsu : MiAdaptadorUsuariosEnEvento = MiAdaptadorUsuariosEnEvento()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios_por_evento)
        supportActionBar?.hide()

        val objIntent: Intent = intent
        var id: String? = objIntent.getStringExtra("id")


        btnSync.setOnClickListener {
            finish()
            startActivity(getIntent())
        }

        cargar_rv_bbdd(id)

    }

    fun cargar_rv_bbdd(id:String?){
        var even:Evento
        if (id != null) {
            db.collection("eventos").document(id).get().addOnSuccessListener {
                var nombre: String? = it.get("nombre") as String?
                var fecha: String? = it.get("fecha") as String?
                var hora: String? = it.get("hora") as String?
                var lat: Double = it.get("lat") as Double
                var lon :Double = it.get("lon") as Double
                var listEve: ArrayList<String> = it.get("listEve") as ArrayList<String>
                var event = Evento(id, nombre.toString(),fecha.toString(),hora.toString(),lat,lon, listEve)
                even = (event)

                for (i in 0 until even.listEve.size) {
                    if (even.listEve[i] != ""){
                        miArrayUsu.add(even.listEve[i])
                    }
                }

                mRecyclerViewUsu = findViewById<RecyclerView>(R.id.RVGestUsusEven)
                mRecyclerViewUsu.setHasFixedSize(true)
                mRecyclerViewUsu.layoutManager = LinearLayoutManager(this)
                mAdapterUsu.MiAdaptadorUsuariosEnEvento(miArrayUsu,this,id!!)
                mRecyclerViewUsu.adapter = mAdapterUsu

            }
        }
    }





}