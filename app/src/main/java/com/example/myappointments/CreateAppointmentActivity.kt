package com.example.myappointments

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_create_appointment.*
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {
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

        btnConfirmAppointment.setOnClickListener {

            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_LONG).show()
            finish()
        }

        val specialtiesOptions= arrayOf("Specialty A", "Specialty B", "Specialty C")
        spinnerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtiesOptions)

        val doctorsOptions = arrayOf("doctor A", "doctor B", "doctor C")
        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorsOptions)
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

        if(cvStep2.visibility == View.VISIBLE){

            cvStep2.visibility = View.GONE
            cvStep1.visibility = View.VISIBLE
        } else if( cvStep1.visibility == View.VISIBLE){
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