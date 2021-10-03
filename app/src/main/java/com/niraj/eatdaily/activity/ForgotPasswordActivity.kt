package com.niraj.eatdaily.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.niraj.eatdaily.R
import com.niraj.eatdaily.fragments.ForgotPasswordInputFragment

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var frameLayout: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        frameLayout = findViewById(R.id.forgotFrame)
        openForgotPasswordInput()

    }
    fun openForgotPasswordInput(){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.forgotFrame, ForgotPasswordInputFragment(this))
        ft.commit()
    }

    override fun onResume() {
        openForgotPasswordInput()
        super.onResume()
    }
}