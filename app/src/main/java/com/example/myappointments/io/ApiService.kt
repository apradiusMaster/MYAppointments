package com.example.myappointments.io

import com.example.myappointments.io.response.LoginResponse
import com.example.myappointments.model.Doctor
import com.example.myappointments.model.Schedule
import com.example.myappointments.model.Specialty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("specialties")

     fun  getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")

     fun  getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

     @GET("shedule/hours")
     fun getHours(@Query("doctor_id") doctoId: Int, @Query("date") date: String): Call<Schedule>

     @POST("login")
     fun  postLogin(@Query( "email") email: String, @Query( "password") password: String): Call<LoginResponse>

     @POST("logout")

     fun postLogout(@Query( "authorization") authHeader: String): Call<Void>

    companion object Factory {
        private const val  BASE_URL = "http://64.227.10.80:8000/api/"

        fun create(): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}