package com.example.myappointments.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myappointments.PreferenceHelper
import com.example.myappointments.PreferenceHelper.set
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.io.response.LoginResponse
import com.example.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvGoToLogin.setOnClickListener {


           Toast.makeText(this, "Ingrese  el correo y contraseña para ingresar", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
        btnConfirmRegister.setOnClickListener{
            performRegister()
        }
    }
    private fun performRegister(){

        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val passwordConfirmation = etRegisterPasswordConfirmation.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()){
            toast(getString(R.string.error_register_empty_fields))
        }
        if (password.length <6){
            toast(getString(R.string.error_register_password_length))
        }

        if (password != passwordConfirmation){
            toast(getString(R.string.error_register_password_do_not_match))
        }

        val call = apiService.postRegister(name,email,password,passwordConfirmation)

        call.enqueue(object: retrofit2.Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.jwt)
                        toast(getString(R.string.welcome_name, loginResponse.user.name))
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


}