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
import com.example.desafio_iii_mgi.FichaUsuarioActivity
import com.example.desafio_iii_mgi.R
import com.example.desafio_iii_mgi.Users.User
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()
class MiAdaptadorRVusus : RecyclerView.Adapter<MiAdaptadorRVusus.ViewHolder>() {

    var usuario: ArrayList<User> = ArrayList()
    lateinit var context: Context

    fun MiAdaptadorRVusus(usuario: ArrayList<User>, context: Context) {
        this.usuario = usuario
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usuario.get(position)
        holder.bind(item, context, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.list_usu, parent, false))
    }


    override fun getItemCount(): Int {
        return usuario.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val correo = view.findViewById(R.id.txtCorreoList) as TextView
        val pill = view.findViewById(R.id.swtListUsu) as Switch


        fun bind(usuario: User, context: Context, adapter: MiAdaptadorRVusus) {
            correo.text = usuario.correo
            pill.isChecked = usuario.verificado == true

            var nombre:String = ""
            var apellidos:String = ""
            var fecha:String = ""
            var verificado:Boolean = false
            var admin:Boolean = false

            db.collection("users").document(correo.text.toString()).get().addOnSuccessListener {
                nombre = (it.get("nombre") as String?).toString()
                apellidos = (it.get("apellidos") as String?).toString()
                fecha = (it.get("edad") as String?).toString()
                verificado = it.get("verificado") == true
                admin = it.get("admin") == true
            }


            itemView.setOnClickListener(View.OnClickListener {
                Toast.makeText(context, correo.text, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, FichaUsuarioActivity::class.java)
                intent.putExtra("correo", correo.text.toString())
                itemView.context.startActivity(intent)
            })

            pill.setOnClickListener(View.OnClickListener {
                if (pill.isChecked){
                    db.collection("users").document(correo.text.toString()).set(
                        hashMapOf(
                            "nombre" to nombre,
                            "apellidos" to apellidos,
                            "edad" to fecha,
                            "verificado" to true,
                            "admin" to admin
                        )
                    )
                }else{
                    db.collection("users").document(correo.text.toString()).set(
                        hashMapOf(
                            "nombre" to nombre,
                            "apellidos" to apellidos,
                            "edad" to fecha,
                            "verificado" to false,
                            "admin" to admin
                        )
                    )
                }
            })

        }

    }
}
