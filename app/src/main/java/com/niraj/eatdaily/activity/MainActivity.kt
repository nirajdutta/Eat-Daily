package com.niraj.eatdaily.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.sip.SipSession
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.niraj.eatdaily.R
import com.niraj.eatdaily.fragments.*
import kotlinx.android.synthetic.main.fragment_my_profile.*

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var imgProfile:ImageView
    lateinit var txtName:TextView
    lateinit var txtPhone:TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        val drawerHeader= navigationView.getHeaderView(0)
        imgProfile=drawerHeader.findViewById(R.id.imgHeaderProfile)
        txtName=drawerHeader.findViewById(R.id.txtHeaderName)
        txtPhone=drawerHeader.findViewById(R.id.txtPhone)
//
        val name=sharedPreferences.getString("name",null)
        val mobileNumber=sharedPreferences.getString("mobile_number",null)
        txtName.text=name
        txtPhone.append(mobileNumber)

        setUpToolbar()
        openHome()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        imgProfile.setOnClickListener {
            val myProfileFragment = MyProfileFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frameLayout, myProfileFragment)
            ft.commit()
            supportActionBar?.title = "My Profile"
            drawerLayout.closeDrawers()
        }
        txtName.setOnClickListener {
            val myProfileFragment = MyProfileFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frameLayout, myProfileFragment)
            ft.commit()
            supportActionBar?.title = "My Profile"
            drawerLayout.closeDrawers()
        }

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true

            previousMenuItem = it
            when (it.itemId) {

                R.id.homeId -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        MyProfileFragment()
                    ).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favorites -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FavoritesFragment()
                    ).commit()
                    supportActionBar?.title = "Favorite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        OrderHistoryFragment()
                    ).commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FaqsFragment()
                    ).commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want exit?")
                    dialog.setPositiveButton("Yes") { test, listener ->

                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        sharedPreferences.edit().clear().apply()
                        finish()
                    }
                    dialog.setNegativeButton("No") { test, listener ->

                        openHome()
                    }
                    dialog.create()
                    dialog.show()
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "EatDaily"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openHome() {
        val fragment = HomeFragment(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.homeId)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
    override fun onResume() {
        window.setSoftInputMode(
            WindowManager.
        LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume()
    }
}