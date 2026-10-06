package com.telecure.app

import android.app.Application
import com.google.firebase.FirebaseApp

class TeleCureApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
