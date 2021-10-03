package com.niraj.eatdaily.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobile: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbarReg)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val upIntent=Intent(this@RegistrationActivity,LoginActivity::class.java)
//        navigateUpTo(upIntent)

        btnRegister.setOnClickListener {
            if (etName.text.toString().length < 3) {
                etName.error = "Invalid Name"
                Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show()
            } else if (etEmail.text.toString().isEmpty() || etMobile.text.contains(
                    ".com",
                    ignoreCase = true
                )
            ) {
//                etEmail.setError("Invalid Email",Drawable.createFromPath(R.drawable.ic_baseline_error_24.toString()))
                etEmail.error = "Invalid Email"
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            } else if (etMobile.text.toString().length < 10) {
                etMobile.error = "Invalid Mobile Number"
                Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (etPassword.text.toString().length < 4) {
                etPassword.error = "Password should be more than or equal 4 digits"
            } else if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
                etPassword.error = "Passwords don't match"
                etConfirmPassword.error = "Passwords don't match"
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                val name = etName.text.toString()
                val mobile = etMobile.text.toString()
                val email = etEmail.text.toString()
                val address = etAddress.text.toString()
                val password = etPassword.text.toString()

                if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
                    try {
                        val url = "http://13.235.250.119/v2/register/fetch_result"
                        val queue = Volley.newRequestQueue(this@RegistrationActivity)
                        val jsonParams = JSONObject()
                        jsonParams.put("name", name)
                        jsonParams.put("mobile_number", mobile)
                        jsonParams.put("password", password)
                        jsonParams.put("address", address)
                        jsonParams.put("email", email)
                        val jsonObjectRequest =
                            object : JsonObjectRequest(Method.POST, url, jsonParams,
                                Response.Listener {
                                    //
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val userJsonObject = data.getJSONObject("data")
                                        sharedPreferences.edit().putBoolean("isLoggedIn", true)
                                            .commit()
                                        sharedPreferences.edit().putString(
                                            "user_id",
                                            userJsonObject.getString("user_id")
                                        ).apply()
                                        sharedPreferences.edit()
                                            .putString("name", userJsonObject.getString("name"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("email", userJsonObject.getString("email"))
                                            .apply()
                                        sharedPreferences.edit().putString(
                                            "mobile_number",
                                            userJsonObject.getString("mobile_number")
                                        ).apply()
                                        sharedPreferences.edit().putString(
                                            "address",
                                            userJsonObject.getString("address")
                                        ).apply()
                                        val intent = Intent(
                                            this@RegistrationActivity,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)
                                    } else {
                                        val response = data.getString("errorMessage")
                                        Toast.makeText(
                                            this,
                                            response,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, Response.ErrorListener {
                                    println(it)
                                    Toast.makeText(
                                        this,
                                        "Volley error occured",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "f26a8500dd363c"
                                    return headers
                                }
                            }
                        queue.add(jsonObjectRequest)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some unexpected error has occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    //TODO
                    Toast.makeText(
                        this@RegistrationActivity,
                        "No Internet Connection!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

//    override fun onBackPressed() {
//        onSupportNavigateUp()
//    }
}