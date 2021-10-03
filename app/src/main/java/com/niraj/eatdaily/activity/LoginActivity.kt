package com.niraj.eatdaily.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etMobile: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)

        etMobile = findViewById(R.id.etMobile)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)


        val intentMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
        if (isLoggedIn) {
            startActivity(intentMainActivity)
            finish()
        }

        btnLogin.setOnClickListener {
            if (ConnectionManager().checkConnectivity(applicationContext as Context)) {
                try {
                    val mobileNumber = etMobile.text.toString()
                    val password = etPassword.text.toString()

                    if (mobileNumber.length == 10 && password.length >= 4) {
                        val url = "http://13.235.250.119/v2/login/fetch_result"
                        val queue = Volley.newRequestQueue(this@LoginActivity)
                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", mobileNumber)
                        jsonParams.put("password", password)

                        val jsonObjectRequest = object :
                            JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                                //retrieve mobile and password from api and compare with input

                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {

                                    val restaurantJsonObject = data.getJSONObject("data")

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).commit()
                                    sharedPreferences.edit().putString("user_id",restaurantJsonObject.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("name", restaurantJsonObject.getString("name"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("email", restaurantJsonObject.getString("email"))
                                        .apply()
                                    sharedPreferences.edit().putString(
                                        "mobile_number",
                                        restaurantJsonObject.getString("mobile_number")
                                    ).apply()
                                    sharedPreferences.edit().putString(
                                        "address",
                                        restaurantJsonObject.getString("address")
                                    ).apply()

                                    startActivity(intentMainActivity)
                                    finish()

                                } else {
                                    println(it)
                                    val response=data.getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        response,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }, Response.ErrorListener {
                                //
                                println(it)
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Volley Error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            //
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "f26a8500dd363c"
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid Phone or Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this@LoginActivity, "No Internet Connection!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        txtSignUp.setOnClickListener {
            val intentRegister = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intentRegister)
        }
        txtForgotPassword.setOnClickListener {
            val intent=Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}