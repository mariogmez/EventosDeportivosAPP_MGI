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



        fun bind(correos: String, context: Context, adapter: MiAdaptadorUsuariosEnEvento, id: String) {

            itemView.setOnLongClickListener(View.OnLongClickListener {

                true
            })
        }

    }
}