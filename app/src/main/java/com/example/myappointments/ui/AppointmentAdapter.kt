package com.example.myappointments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myappointments.R
import com.example.myappointments.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter
    :  RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    var appointments = ArrayList<Appointment>()

    class  ViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {

         /* val tvAppointmentId = itemView.tvAppointmentId
          val tvDoctorName = itemView.tvDoctorName
          val tvScheduledDate = itemView.tvScheduledDate
          val  tvScheduledTime = itemView.tvScheduledTime */

        fun bind( appointment : Appointment) = with(itemView){

                tvAppointmentId.setText( context.getString(R.string.item_appointment_id, appointment.id))
                tvDoctorName.setText(appointment.doctor.name)
                tvScheduledDate.setText(context.getString(R.string.item_appointment_date, appointment.scheduledDate))
                tvScheduledTime.setText(context.getString(R.string.item_appointment_time, appointment.scheduledTime))

                tvSpecialty.setText(appointment.specialty.name)
                tvDescription.setText(appointment.description)
                tvStatus.setText(appointment.status)
                tvType.setText(appointment.type)
                tvCreatedAt.setText(context.getString(R.string.item_appointment_created_at, appointment.createdAt))

                ibExpand.setOnClickListener {
                   if(linearLayoutDetails.visibility == View.VISIBLE){
                       linearLayoutDetails.visibility = View.GONE
                       ibExpand.setImageResource(R.drawable.ic_expand_more)
                   } else{
                       linearLayoutDetails.visibility = View.VISIBLE
                       ibExpand.setImageResource(R.drawable.ic_expand_less)
                         }

                }


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