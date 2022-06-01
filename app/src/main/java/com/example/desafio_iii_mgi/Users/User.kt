package com.example.desafio_iii_mgi.Users

data class User(var correo:String, var nombre:String, var apellidos:String, var edad:String, val verificado: Boolean?, val admin: Boolean?, val listUsu: ArrayList<String>)