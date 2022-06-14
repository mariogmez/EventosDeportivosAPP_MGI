package com.example.desafio_iii_mgi.Adaptadores

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio_iii_mgi.Events.Evento
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()
class MiAdaptadorUsuariosEnEvento : RecyclerView.Adapter<MiAdaptadorUsuariosEnEvento.ViewHolder>(){


    var usuario: ArrayList<User> = ArrayList()
    lateinit var id: String
    lateinit var context: Context

    fun MiAdaptadorUsuariosEnEvento(usuario: ArrayList<User>, context: Context, id:String) {
        this.usuario = usuario
        this.context = context
        this.id = id
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usuario.get(position)
        holder.bind(item, context, this, id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.list_usu_gest,parent, false)
        )
    }



    override fun getItemCount(): Int {
        return usuario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreEV = view.findViewById(R.id.txtNombreListUsu) as TextView
        val correoEV = view.findViewById(R.id.txtCorreoListUsu) as TextView
        val apellidoEV = view.findViewById(R.id.txtApellidosListUsu) as TextView


        var nombreEvento:String = ""
        var fechaEvento:String = ""
        var horaEvento:String = ""
        var latEvento:Double = 0.0
        var lonEvento:Double = 0.0
        var listEvento:ArrayList<String> = arrayListOf()





        fun bind(usuario: User, context: Context, adapter: MiAdaptadorUsuariosEnEvento, id: String) {

            nombreEV.text = usuario.nombre
            correoEV.text = usuario.correo
            apellidoEV.text = usuario.apellidos



            db.collection("eventos").document(id).get().addOnSuccessListener {
                nombreEvento = (it.get("nombre") as String?).toString()
                fechaEvento = (it.get("fecha") as String?).toString()
                horaEvento = (it.get("hora") as String?).toString()
                latEvento = it.get("lat") as Double
                lonEvento = it.get("lon") as Double
                listEvento = it.get("listEve") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */

            }



            itemView.setOnClickListener(View.OnClickListener {
                Toast.makeText(context, id, Toast.LENGTH_SHORT).show()
            })

            itemView.setOnLongClickListener(View.OnLongClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alerta")
                builder.setMessage("¿Esta seguro de establecer esta ubicacion para el evento?")
                builder.setPositiveButton("Si",{ dialogInterface: DialogInterface, i: Int ->




                    if (listEvento.contains(usuario.correo)){
                        listEvento.remove(usuario.correo)
                        for (i in 0..listEvento.size -1 ){
                            Toast.makeText(context, listEvento[i], Toast.LENGTH_SHORT).show()
                        }

                        db.collection("eventos").document(id).set(
                            hashMapOf(
                                "nombre" to nombreEvento,
                                "fecha" to fechaEvento,
                                "hora" to horaEvento,
                                "lat" to latEvento,
                                "lon" to lonEvento,
                                "listEve" to listEvento
                            )
                        )

                    }


                })

                builder.setNegativeButton("No",{ dialogInterface: DialogInterface, i: Int ->

                })
                builder.show()



                true
            })
        }

    }
}