package com.example.desafio_iii_mgi

import android.app.Application

class PrecargasApp : Application() {
    companion object{
        lateinit var prefs:Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}