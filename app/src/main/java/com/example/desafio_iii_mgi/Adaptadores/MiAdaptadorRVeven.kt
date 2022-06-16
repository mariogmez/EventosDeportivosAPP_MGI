package com.example.desafio_iii_mgi.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.Events.EventoActivity
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()
class MiAdaptadorRVeven : RecyclerView.Adapter<MiAdaptadorRVeven.ViewHolder>(){

    var evento: ArrayList<Evento> = ArrayList()
    lateinit var context: Context

    fun MiAdaptadorRVeven(evento: ArrayList<Evento>, context: Context) {
        this.evento = evento
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = evento.get(position)
        holder.bind(item, context, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.list_event,parent, false)
        )
    }



    override fun getItemCount(): Int {
        return evento.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreEV = view.findViewById(R.id.txtEventoList) as TextView
        val pill = view.findViewById(R.id.pillActivar) as Switch



        fun bind(evento: Evento, context: Context, adapter: MiAdaptadorRVeven) {
            nombreEV.text = evento.nombre
            pill.isChecked = evento.activado == true

            pill.setOnClickListener(View.OnClickListener {
                if (pill.isChecked){
                    modificar_verificado(evento, true)
                }else{
                    modificar_verificado(evento, false)
                }
            })
            
            itemView.setOnLongClickListener {

                Toast.makeText(context, "borrar gracias", Toast.LENGTH_SHORT).show()
                true
            }

            itemView.setOnClickListener(View.OnClickListener {

                val intent = Intent(context, EventoActivity::class.java)
                intent.putExtra("id", evento.idEvento.toString())
                itemView.context.startActivity(intent)

            })

        }
        
        private fun modificar_verificado(event: Evento, bol:Boolean){
            db.collection("eventos").document(event.idEvento).set(
                hashMapOf(
                    "nombre" to event.nombre,
                    "fecha" to event.fecha,
                    "hora" to event.hora,
                    "lat" to event.lat,
                    "lon" to event.lon,
                    "listEve" to event.listEve,
                    "activado" to bol

                )
            )
        }

    }
}