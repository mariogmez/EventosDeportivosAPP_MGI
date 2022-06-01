package com.example.desafio_iii_mgi.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Adaptadores.*
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.Fragments.DatePickerFragment
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Fragments.TimePickerFragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_dialog.view.*
import kotlinx.android.synthetic.main.activity_listado_eventos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LIstadoEventosActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()// <- Inicializamos la BBDD

    /*
     * DECLARAMOS LA VARIBLES NECESARIAS PARA UTILIZAR
     * RECYCLERVIEW @MiApadatorRVeven
     */
    lateinit var mRecyclerViewEvent: RecyclerView
    var miArrayEvent:ArrayList<Evento> = ArrayList()
    val mAdapterEvent : MiAdaptadorRVeven = MiAdaptadorRVeven()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_eventos)
        supportActionBar?.hide()


        // MUESTRA UN DIALOG MODIFICADO PARA AÑADIR NUEVOS EVENTOS
        btnFlotante.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_dialog,null)

            builder.setView(view) // <- se le pasa la vista creada al builder
            val dialog = builder.create() //<- se crea el dialog
            dialog.show() //<- se muestra el showdialog

            val cajaFechaSWD = view.txtFechaDLG
            val cajaHoraSWD = view.txtHoraDLG
            val cajaNombreSWD = view.txtNombreDLG

            cajaFechaSWD.setOnClickListener{
                showDatePickerDIalog(cajaFechaSWD)
            }

            cajaHoraSWD.setOnClickListener{
                showTimePickerDialog(cajaHoraSWD)
            }

            var lon:Double = 0.0
            var lat:Double = 0.0

            // AÑADE LOS CAMPOS DEL DIALOG A FIREBASE
            view.btnConfirmarDLG.setOnClickListener {
                db.collection("eventos").document().set(
                    hashMapOf(
                        "nombre" to cajaNombreSWD.text.toString(),
                        "fecha" to cajaFechaSWD.text.toString(),
                        "hora" to cajaHoraSWD.text.toString(),
                        "lat" to lat,
                        "lon" to lon,
                        "listEve" to arrayListOf("")

                    )
                )
                Toast.makeText(this, "Evento creado correctamente...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LIstadoEventosActivity::class.java)
                startActivity(intent)
            }
        }

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
        mRecyclerViewEvent = findViewById<RecyclerView>(R.id.MiRecyclerVWeventos)
        mRecyclerViewEvent.setHasFixedSize(true)
        mRecyclerViewEvent.layoutManager = LinearLayoutManager(this)
        mAdapterEvent.MiAdaptadorRVeven(miArrayEvent, this)
        mRecyclerViewEvent.adapter = mAdapterEvent


    }


    /*
     * LLAMA A LOS FRAGMENTS CORRESPONDIENTES
     * PARA MOSTRAR UN DIALOG MODIFICADO QUE
     * PIDE LA HORA
     */
    private fun showTimePickerDialog(caja:EditText) {
        val timePicker = TimePickerFragment{hour, minute -> onTimeSelected(hour, minute,caja)}
        timePicker.show(supportFragmentManager, "timePicker")
    }

    fun onTimeSelected(hour:Int,minute:Int, caja:EditText){ // <- ENCARGADO DE PINTAR EL RESULTADO EN LA CAJA

        //CONTROLAMOS QUE PUEDAN TENER 0 ANTES DEL NUMERO
        var horaStr = ""+hour
        if  (horaStr.length == 1){
            horaStr = "0$horaStr"
        }

        var minuteStr = ""+minute
        if  (minuteStr.length == 1){
            minuteStr = "0$minuteStr"
        }

        caja.setText("" +horaStr + ":" + minuteStr)
    }

    /*
     * LLAMA A LOS FRAGMENTS CORRESPONDIENTES
     * PARA MOSTRAR UN DIALOG MODIFICADO QUE
     * PIDE LA FECHA
     */
    private fun showDatePickerDIalog(caja:EditText) {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected(day, month, year,caja)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day:Int, month:Int, year:Int,caja:EditText){ // <- ENCARGADO DE PINTAR EL RESULTADO EN LA CAJA
        caja.setText("" +day+ "/" +month+ "/" +year)
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
                miArrayEvent.add(al)
            }
        }
    }
}