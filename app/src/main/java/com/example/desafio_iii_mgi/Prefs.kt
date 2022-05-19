package com.example.desafio_iii_mgi

import android.content.Context
import java.security.AccessControlContext

class Prefs (val context: Context) {

    val SHARED_NAME = "Mydtb"
    val SHARED_CORREO = "correo"
    val SHARED_PWD = "pwd"
    val SHARED_RECOR = "recor"

    //CREAMOS LA BASE DE DATOS
    val storage = context.getSharedPreferences(SHARED_NAME, 0)


    /*
     * FUN GUARDA VALORES
     */
    fun saveCorreo(correo:String){
        storage.edit().putString(SHARED_CORREO,correo).apply()
    }

    fun savePwd(pwd:String){
        storage.edit().putString(SHARED_PWD,pwd).apply()
    }

    fun saveRecordar(recor:Boolean){
        storage.edit().putBoolean(SHARED_RECOR, recor).apply()
    }

    /*
     * FUN DEVUELVE VALORES
     */

    fun getCorreo():String{
        return storage.getString(SHARED_CORREO, "")!!
    }

    fun getPwd():String{
        return  storage.getString(SHARED_PWD, "")!!
    }

    fun getRecor():Boolean{
        return  storage.getBoolean(SHARED_RECOR, false)
    }

    fun wipe(){
        storage.edit().clear().apply()
    }
}