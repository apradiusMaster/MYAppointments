package com.example.myappointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myappointments.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(  private val appointments: ArrayList<Appointment> ) :  RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class  ViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {

         /* val tvAppointmentId = itemView.tvAppointmentId
          val tvDoctorName = itemView.tvDoctorName
          val tvScheduledDate = itemView.tvScheduledDate
          val  tvScheduledTime = itemView.tvScheduledTime */

        fun bind( appointment : Appointment) = with(itemView){

                tvAppointmentId.setText( context.getString(R.string.item_appointment_id, appointment.id))
                tvDoctorName.setText(appointment.doctorName)
                tvScheduledDate.setText(context.getString(R.string.item_appointment_date, appointment.scheduledDate))
                tvScheduledTime.setText(context.getString(R.string.item_appointment_time, appointment.scheduledTime))

        }

    }


    // Inflate XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

          return  ViewHolder(

               LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
          )

    }


    // binds data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appointment = appointments[position]

        holder.bind(appointment)

      /*  holder.tvAppointmentId.setText("Cita # ${appointment.id.toString()}")
        holder.tvDoctorName.setText(appointment.doctorName)
        holder.tvScheduledDate.setText("Para el día ${appointment.scheduledDate}")
        holder.tvScheduledTime.setText("A las ${appointment.scheduledTime}") */




    }

    // Number of elements
    override fun getItemCount() = appointments.size

}