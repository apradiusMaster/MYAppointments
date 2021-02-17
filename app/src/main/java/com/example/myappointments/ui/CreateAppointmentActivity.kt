package com.example.myappointments.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.DnsResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.model.Specialty
import kotlinx.android.synthetic.main.card_view_step_once.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService : ApiService by  lazy {
        ApiService.create()
    }
     private val selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {

            if(etDescription.text.toString().length <=5){
                 etDescription.error = getString(R.string.validate_appointment_description)
            }else{
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }
        btnNext2.setOnClickListener{

            showAppointmentDataConfirm()
            cvStep2.visibility = View.GONE
            cvStep3.visibility = View.VISIBLE
        }

        btnConfirmAppointment.setOnClickListener {

            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_LONG).show()
            finish()
        }

        loadSpecialties()



        val doctorsOptions = arrayOf("doctor A", "doctor B", "doctor C")
        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorsOptions)
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

                      val specialtyOptions = ArrayList<String>()
                      specialties?.forEach {
                          specialtyOptions.add(it.name)
                      }
                      spinnerSpecialties.adapter = ArrayAdapter<String>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialtyOptions)
                  }
              }

              override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                  Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_SHORT).show()
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
             tvCofirmDate.text = etSheduledDate.text.toString()
             tvCofirmTime.text = selectedRadioButton?.text.toString()
    }

    fun onClickSheduledDate( v: View?){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener{ datepicker, y , m ,d ->

            //Toast.makeText( this, " $y-$m-$d", Toast.LENGTH_SHORT).show()

            selectedCalendar.set(y,m,d)
            etSheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            displayRadioButtons()
        }
        // new dialog
       val datePickerDialog = DatePickerDialog(this, listener , year, month, dayOfMonth)

         //set limits

        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,1)
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis

        // show dialog
        datePickerDialog.show()

    }
    private fun  displayRadioButtons(){

        // radioGroup.clearCheck()
        //radioGroup.removeAllViews()
        selectedRadioButton = null

        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM" ,"3:30 PM", "4:00 PM", "4:30 PM")
         var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it


            radioButton.setOnClickListener { view ->
                    selectedRadioButton?.isChecked = false
                    selectedRadioButton = view as  RadioButton?
                    selectedRadioButton?.isChecked = true
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