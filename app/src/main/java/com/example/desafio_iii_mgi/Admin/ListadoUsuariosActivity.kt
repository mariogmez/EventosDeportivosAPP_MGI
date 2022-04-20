package com.example.desafio_iii_mgi.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Adaptadores.MiAdaptadorRVusus
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

class ListadoUsuariosActivity : AppCompatActivity() {

    private val db = Firebase.firestore // <- Inicializamos la BBDD

    /*
     * DECLARAMOS LA VARIBLES NECESARIAS PARA UTILIZAR
     * RECYCLERVIEW @MiApadatorRVusus
     */
    lateinit var mRecyclerViewUsu: RecyclerView
    var miArrayUsu:ArrayList<User> = ArrayList()
    val mAdapterUsu : MiAdaptadorRVusus = MiAdaptadorRVusus()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_usuarios)
        supportActionBar?.hide()

        /*
         * INICIALIZAMOS LA CORRUTINA PARA LA CARGA
         * DEL RECYLERVIEW @MiApadatorRVusus
         */
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot
                obtenerDatos(datos as QuerySnapshot?)
            }
            job.join()
        }
        mRecyclerViewUsu = findViewById<RecyclerView>(R.id.MiRecyclerVWusuarios)
        mRecyclerViewUsu.setHasFixedSize(true)
        mRecyclerViewUsu.layoutManager = LinearLayoutManager(this)
        mAdapterUsu.MiAdaptadorRVusus(miArrayUsu, this)
        mRecyclerViewUsu.adapter = mAdapterUsu

    }

    /*
     * FUNCIONES COMPLEMENTARIAS A LA CORRUTINA
     * ENCARGADAS DE ACCEDER A FIREBASE Y
     * RECUPERAR LA INFORMACION
     */
    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("users")
                .whereEqualTo("admin",false)
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

                var al = User(
                    dc.document.id.toString(),
                    dc.document.get("nombre").toString(),
                    dc.document.get("apellidos").toString(),
                    dc.document.get("edad").toString(),
                    dc.document.getBoolean("verificado"),
                    dc.document.getBoolean("admin")

                )
                miArrayUsu.add(al)
            }
        }
    }
}