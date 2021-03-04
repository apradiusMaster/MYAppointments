package com.example.myappointments.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myappointments.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.example.myappointments.PreferenceHelper.get
import com.example.myappointments.PreferenceHelper.set
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.io.response.LoginResponse
import com.example.myappointments.util.toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService : ApiService by lazy {
        ApiService.create()
    }

    private  val snackBar  by lazy {
        Snackbar.make(mainLayout, R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //val  preferences = getSharedPreferences("general ", Context.MODE_PRIVATE)
         //val   session = preferences.getBoolean("session", false)
        val preferences = PreferenceHelper.defaultPrefs(this)
         if (preferences["jwt", ""].contains( "."))
             goToMenuActivity()

        btnLogin.setOnClickListener {
            // validates
            performLogin()
        }

        tvGoToRegister.setOnClickListener {
            Toast.makeText(this, getString(R.string.please_fill_your_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){

        val call = apiService.postLogin(etEmail.text.toString(), etPassword.text.toString())
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){

                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.jwt)
                        goToMenuActivity()
                    } else {
                            toast(getString(R.string.error_invalid_credentials))
                        }
                } else {
                    toast(getString(R.string.error_login_response))
                    }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })

    }

     private fun createSessionPreference( jwt : String){
         /*
           val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
          val  editor = preferences.edit()
          editor.putBoolean( "session", true)
          editor.apply() */

         val preferences = PreferenceHelper.defaultPrefs(this)
         preferences["jwt"] = jwt


     }
     private fun goToMenuActivity(){
         val intent = Intent(this, MenuActivity::class.java)
         startActivity(intent)
         finish()
     }

    override fun onBackPressed() {
        if (snackBar.isShown)
        super.onBackPressed()
        else
            snackBar.show()
    }
}