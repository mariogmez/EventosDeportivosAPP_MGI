package com.example.desafio_iii_mgi.Adaptadores

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Admin.LocalizacionUsuariosActivity
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()
class MiAdaptadorUsuariosEnEvento : RecyclerView.Adapter<MiAdaptadorUsuariosEnEvento.ViewHolder>(){


    var correos: ArrayList<String> = ArrayList()
    lateinit var id: String
    lateinit var context: Context

    fun MiAdaptadorUsuariosEnEvento(correos: ArrayList<String>, context: Context, id:String) {
        this.correos = correos
        this.context = context
        this.id = id
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = correos.get(position)
        holder.bind(item, context, this, id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.list_usu_gest,parent, false)
        )
    }



    override fun getItemCount(): Int {
        return correos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val correo = view.findViewById(R.id.txtCorreoListUsu) as TextView
        val linear = view.findViewById(R.id.linearUsus) as LinearLayout

        fun bind(correos: String, context: Context, adapter: MiAdaptadorUsuariosEnEvento, id: String) {

            correo.text = correos

            itemView.setOnClickListener {
                val intent = Intent(context, LocalizacionUsuariosActivity::class.java)
                intent.putExtra("correo", correos)
                itemView.context.startActivity(intent)
            }

            itemView.setOnLongClickListener(View.OnLongClickListener {
                mostrar_emergente(context,id, correos)
                true
            })
        }


        /**
         ** FUNCIONES
         **/
        fun mostrar_emergente(context: Context, id: String, correos:String){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alerta")
            builder.setMessage("¿Esta seguro de querer expulsar este usuario?")
            builder.setPositiveButton("Si",{ dialogInterface: DialogInterface, i: Int ->
                obtener_bbdd(id, correos)
                linear.isVisible = false //<-- ocultamos el linear al borrarlo
            })

            builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int ->
            })
            builder.show()
        }

        fun obtener_bbdd(id:String, correos:String){
            db.collection("eventos").document(id).get().addOnSuccessListener {
                var nombre: String? = it.get("nombre") as String?
                var fecha: String? = it.get("fecha") as String?
                var hora: String? = it.get("hora") as String?
                var lat: Double = it.get("lat") as Double
                var lon :Double = it.get("lon") as Double
                var listEve: ArrayList<String> = it.get("listEve") as ArrayList<String>
                var event = Evento(id, nombre.toString(),fecha.toString(),hora.toString(),lat,lon, listEve)

                if (correos != null) {event.listEve.remove(correos)}
                insertar_bbdd(id,correos,event)

            }
        }

        fun insertar_bbdd(id:String, correos:String, event:Evento){
            db.collection("eventos").document(id).set(
                hashMapOf(
                    "nombre" to event.nombre,
                    "fecha" to event.fecha,
                    "hora" to event.hora,
                    "lat" to event.lat,
                    "lon" to event.lon,
                    "listEve" to event.listEve
                )
            )
        }

    }




}