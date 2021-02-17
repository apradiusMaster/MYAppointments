package com.example.myappointments.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappointments.R
import com.example.myappointments.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val  appointments = ArrayList<Appointment>()

        appointments.add(
            Appointment(1, "Medico Test","12/02/2021", "3:00 PM")
        )
        appointments.add(
            Appointment(2, "Medico BB", "12/02/2021", "3:30 PM")
        )
        appointments.add(
            Appointment(3, "Medico CC", "12:02/2021", "4:00 PM")

        )


        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = AppointmentAdapter( appointments)
    }
}