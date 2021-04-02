package com.example.myappointments.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.myappointments.PreferenceHelper
import com.example.myappointments.PreferenceHelper.get
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.io.response.SimpleResponse
import com.example.myappointments.model.Doctor
import com.example.myappointments.model.Schedule
import com.example.myappointments.model.Specialty
import com.example.myappointments.util.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.card_view_step_once.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.item_appointment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService : ApiService by  lazy {
        ApiService.create()
    }
    private val preferences by  lazy {
        PreferenceHelper.defaultPrefs(this)
    }
     private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeBtn: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {

            if(etDescription.text.toString().length <=5){
                 etDescription.error = getString(R.string.validate_appointment_description)
            }else{
                // continue to step 2
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }
        btnNext2.setOnClickListener{

            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error= getString(R.string.validate_appointment_date)
                }
                selectedTimeBtn == null -> {
                    Snackbar.make(createAppointmentLinearLayout, R.string.validate_appointment_time, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    // continue to step 3
                    showAppointmentDataConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            performStoreAppointment()

        }



        loadSpecialties()
        listenSpecialtyChanges()
        listenDoctorAndDateChanges()

    }

    private  fun  performStoreAppointment(){
        btnConfirmAppointment.isClickable = false

        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"
        val description = tvConfirmDescription.text.toString()
        val specialty = spinnerSpecialties.selectedItem as Specialty
        val doctor = spinnerDoctors.selectedItem as Doctor
        val scheduledDate = tvConfirmDate.text.toString()
        val scheduledTime = tvConfirmTime.text.toString()
        val type = tvConfirmType.text.toString()

        val call = apiService.storeAppointments(
                authHeader, description,
                specialty.id, doctor.id,
                scheduledDate, scheduledTime,
                type)

        call.enqueue(object: retrofit2.Callback<SimpleResponse>{
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful){
                    toast(getString(R.string.create_appointments_success))
                    finish()
                } else{
                    toast(getString(R.string.create_appointments_error))
                    btnConfirmAppointment.isClickable = true
                    }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })





    }

    private fun listenDoctorAndDateChanges(){
        //doctors
        spinnerDoctors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        // scheduled date
        etScheduledDate.addTextChangedListener(object:   TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

        })
    }

    private  fun loadHours( doctorId: Int, date: String ){
        if (date.isEmpty()){
            return
        }

        val call = apiService.getHours(doctorId, date)
        call.enqueue(object : retrofit2.Callback<Schedule>{
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful){
                    val schedule = response.body()
                    //Toast.makeText(this@CreateAppointmentActivity, "morning: ${schedule?.mornig?.size}, afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                    //val hours = arrayOf("3:00 PM" ,"3:30 PM", "4:00 PM", "4:30 PM")
                   schedule?.let {
                       tvSelectDoctorAndDate.visibility = View.GONE

                        val intervals = it.morning + it.afternoon
                        val hours = ArrayList<String>()
                        intervals.forEach { interval ->
                            hours.add(interval.start)
                        }
                        displayIntervalRadios(hours)
                    }
                }
            }

            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_hours), Toast.LENGTH_SHORT).show()
            }

        })
        //Toast.makeText(this, "doctor: $doctorId, date: $date", Toast.LENGTH_SHORT).show()
    }


    private fun loadSpecialties(){

       val call = apiService.getSpecialties()

          call.enqueue(object:  retrofit2.Callback<ArrayList<Specialty>> {
              override fun onResponse(
                  call: Call<ArrayList<Specialty>>,
                  response: Response<ArrayList<Specialty>>
              ) {
                  if (response.isSuccessful){
                      val specialties = response.body()
                      /*
                      val specialtyOptions = ArrayList<String>()
                      specialties?.forEach {
                          specialtyOptions.add(it.name)
                      }*/
                      spinnerSpecialties.adapter = specialties?.let {
                          ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                              it
                          )
                      }
                  }
              }

              override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                  Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_SHORT).show()
                  finish()
              }

          })

    }

    private fun listenSpecialtyChanges(){
        spinnerSpecialties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                 val specialty = adapter?.getItemAtPosition(position) as Specialty
                 //Toast.makeText(this@CreateAppointmentActivity, "id: ${specialty.id}", Toast.LENGTH_SHORT).show()
                loadDoctors(specialty.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
    private fun loadDoctors(specialtyId: Int){
        val call = apiService.getDoctors(specialtyId)
        call.enqueue(object : retrofit2.Callback<ArrayList<Doctor>> {
            override fun onResponse(
                call: Call<ArrayList<Doctor>>,
                response: Response<ArrayList<Doctor>>
            ) {
                if (response.isSuccessful){
                    val  doctors = response.body()
                    spinnerDoctors.adapter = doctors?.let {
                        ArrayAdapter<Doctor>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                            it
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_doctors), Toast.LENGTH_SHORT).show()
                finish()
            }

        })
    }

    private  fun showAppointmentDataConfirm(){
         tvConfirmDescription.text = etDescription.text.toString()
         tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioBtnId = radioGroupType.checkedRadioButtonId
         val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnId)
         tvConfirmType.text = selectedRadioType.text.toString()
         tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
             tvConfirmDate.text = etScheduledDate.text.toString()
             tvConfirmTime.text = selectedTimeBtn?.text.toString()
    }

    fun onClickScheduledDate( v: View?){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener{ datepicker, y , m ,d ->

            //Toast.makeText( this, " $y-$m-$d", Toast.LENGTH_SHORT).show()

            selectedCalendar.set(y,m,d)
            etScheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    (m+1).twoDigits(),
                    d.twoDigits()
                )
            )
        }
        // new dialog
       val datePickerDialog = DatePickerDialog(this, listener , year, month, dayOfMonth)

         //set limits

        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,1) // +1
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis // +30

        // show dialog
        datePickerDialog.show()

    }
    private fun  displayIntervalRadios(  hours: ArrayList<String>){

        // radioGroup.clearCheck()
        //radioGroup.removeAllViews()
        selectedTimeBtn = null

        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        if (hours.isEmpty() ) {
            tvNotAvailableHours.visibility = View.VISIBLE
            return
        }
         tvNotAvailableHours.visibility = View.GONE

        //val hours = arrayOf("3:00 PM" ,"3:30 PM", "4:00 PM", "4:30 PM")
         var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it


            radioButton.setOnClickListener { view ->
                    selectedTimeBtn?.isChecked = false
                    selectedTimeBtn = view as  RadioButton?
                    selectedTimeBtn?.isChecked = true
            }
            if (goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)

            goToLeft = !goToLeft

        }
    }

    private fun Int.twoDigits()

     = if(this >=10) this.toString() else "0$this"


    override fun onBackPressed() {

        when {
            cvStep3.visibility == View.VISIBLE -> {

                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE

            }
            cvStep2.visibility == View.VISIBLE -> {

                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE
            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)

                builder.setTitle("Estas seguro que desea eliminar")
                builder.setMessage("Si acepta los datos se perderan")
                builder.setPositiveButton("Si, acepto"){ _ , _ ->
                    finish()
                }

                builder.setNegativeButton("Desea cancelar"){ dialog, _ ->

                    dialog.dismiss()
                }


                val dialog =builder.create()

                dialog.show()

            }
        }



    }
}