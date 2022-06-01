package com.example.desafio_iii_mgi.Users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorEvenUsus
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_listado_eventos_usus.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ListadoEventosUsusAct : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()// <- Inicializamos la BBDD

    /*
     * DECLARAMOS LA VARIBLES NECESARIAS PARA UTILIZAR
     * RECYCLERVIEW @MiApadatorRVeven
     */
    lateinit var mRecyclerViewEventUsu: RecyclerView
    var miArrayEventUsu:ArrayList<Evento> = ArrayList()
    val mAdapterEventUsu : MiAdaptadorEvenUsus = MiAdaptadorEvenUsus()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_eventos_usus)

        val objIntent: Intent = intent
        var correo: String? = objIntent.getStringExtra("correo")

        supportActionBar?.hide()

        /*
         * INICIALIZAMOS LA CORRUTINA PARA LA CARGA
         * DEL RECYLERVIEW @MiApadatorRVeven
         */
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot
                obtenerDatos(datos as QuerySnapshot?)
            }
            job.join()
        }
        mRecyclerViewEventUsu = findViewById<RecyclerView>(R.id.MiRecyclerVWeventosUsu)
        mRecyclerViewEventUsu.setHasFixedSize(true)
        mRecyclerViewEventUsu.layoutManager = LinearLayoutManager(this)
        mAdapterEventUsu.MiAdaptadorEvenUsus(miArrayEventUsu, this, correo)
        mRecyclerViewEventUsu.adapter = mAdapterEventUsu


    }

    /*
     * FUNCIONES COMPLEMENTARIAS A LA CORRUTINA
     * ENCARGADAS DE ACCEDER A FIREBASE Y
     * RECUPERAR LA INFORMACION
     */
    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("eventos")
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                var al = Evento(
                    dc.document.id.toString(),
                    dc.document.get("nombre").toString(),
                    dc.document.get("fecha").toString(),
                    dc.document.get("hora").toString(),
                    dc.document.get("lat") as Double,
                    dc.document.get("lon") as Double,
                    dc.document.get("listEve") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                )
                miArrayEventUsu.add(al)
            }
        }
    }
}