package com.example.myappointments

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import com.example.myappointments.PreferenceHelper.set
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        btnCreateAppointment.setOnClickListener {

            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointment.setOnClickListener {

            val intent = Intent(this , AppointmentsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {

            closeSessionPreference()
            val intent = Intent(this,  MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private  fun closeSessionPreference(){
        /*
          val preferences = getSharedPreferences("general ",  Context.MODE_PRIVATE)
          val editor = preferences.edit()
         editor.putBoolean("session", false)
        editor.apply() */
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["session"] = false
    }
}