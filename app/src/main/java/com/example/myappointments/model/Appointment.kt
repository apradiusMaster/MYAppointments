package com.example.myappointments.model

import com.google.gson.annotations.SerializedName

data class Appointment (
     val id: Int,
     val  description: String,
     val type: String,
     val status: String,

      @SerializedName("scheduled_date")  val scheduledDate: String,
      @SerializedName("scheduled_time") val scheduledTime: String,
     @SerializedName("created_at") val createdAt: String,

      val specialty: Specialty,
      val doctor: Doctor
     )