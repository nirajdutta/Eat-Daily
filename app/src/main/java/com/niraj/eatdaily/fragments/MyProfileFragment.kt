package com.niraj.eatdaily.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.niraj.eatdaily.R


class MyProfileFragment : Fragment() {

    lateinit var imgProfile:ImageView
    lateinit var txtName:TextView
    lateinit var txtMobileNumber:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_my_profile, container, false)
        sharedPreferences=
            activity?.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
                ?:sharedPreferences
        imgProfile=view.findViewById(R.id.imgProfile)
        txtName=view.findViewById(R.id.txtName)
        txtMobileNumber=view.findViewById(R.id.txtMobileNumber)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)

        val name=sharedPreferences.getString("name","John Doe")
        val email=sharedPreferences.getString("email","john@doe.com")
        val mobileNumber=sharedPreferences.getString("mobile_number","9998886666")
        val address=sharedPreferences.getString("address","Gurugram")

        txtName.text=name
        txtMobileNumber.append(mobileNumber)
        txtEmail.text=email
        txtAddress.text=address
        return view
    }
}