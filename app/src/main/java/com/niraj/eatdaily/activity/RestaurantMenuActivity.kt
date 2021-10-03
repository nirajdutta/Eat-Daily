package com.niraj.eatdaily.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.adapter.RestaurantMenuAdapter
import com.niraj.eatdaily.model.RestaurantMenu
import com.niraj.eatdaily.util.ConnectionManager
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId:String
    lateinit var restaurantName:String
//    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var btnProceedToCart: Button
    lateinit var restaurantMenuProgressdialog:RelativeLayout

    var restaurantMenuList= arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        toolbar=findViewById(R.id.toolBar)
        recyclerView=findViewById(R.id.recyclerViewRestaurantMenu)
        layoutManager=LinearLayoutManager(this)

        restaurantId= intent.getStringExtra("restaurantId").toString()
        restaurantName=intent.getStringExtra("restaurantName").toString()


//        proceedToCartLayout=findViewById(R.id.rlProceedToCart)
        btnProceedToCart=findViewById(R.id.btnProceedToCart)
        restaurantMenuProgressdialog=findViewById(R.id.restaurantMenuProgressDialog)
        restaurantMenuProgressdialog.visibility=View.VISIBLE
        setUpToolBar()

        toolbar.setNavigationOnClickListener {
            if (menuAdapter.getSelectedItemCount() > 0) {


                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                alterDialog.setTitle("Alert!")
                alterDialog.setMessage("Going back will remove everything from cart")
                alterDialog.setPositiveButton("Okay") { text, listener ->
                    super.onBackPressed()
                }
                alterDialog.setNegativeButton("No") { text, listener ->

                }
                alterDialog.show()
            } else {
                super.onBackPressed()
            }
        }


        if (ConnectionManager().checkConnectivity(this)){
            try {
                val url="http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
                val queue=Volley.newRequestQueue(this)
                val jsonObjectRequest=object :JsonObjectRequest(Method.GET,url,null, Response.Listener {
                    val responseJsonObject=it.getJSONObject("data")
                    val success=responseJsonObject.getBoolean("success")
                    if (success){
                        restaurantMenuList.clear()
                        val data=responseJsonObject.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val menuJsonObject=data.getJSONObject(i)
                            val menuItem=RestaurantMenu(
                                menuJsonObject.getString("id"),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one")
                            )
                            restaurantMenuList.add(menuItem)
                            menuAdapter= RestaurantMenuAdapter(
                                this,
                                restaurantId,
                                restaurantName,
                                /*proceedToCartLayout,*/
                                btnProceedToCart,
                                restaurantMenuList
                            )

                            recyclerView.adapter=menuAdapter
                            recyclerView.layoutManager=layoutManager


                        }
                        restaurantMenuProgressdialog.visibility=View.GONE
                    }else{
                        Toast.makeText(this,"Some error occurred :${responseJsonObject.getString("errorMessage")}",Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this,"Volley error occurred",Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "f26a8500dd363c"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                Toast.makeText(this, e.localizedMessage?.toString(),Toast.LENGTH_SHORT).show()
            }
        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { test, listener ->
                //Code to open settings
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { test, listener ->
                //Code to exit
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }


    }
    fun getData(){
        if (ConnectionManager().checkConnectivity(this)){
            try {
                val url=
                    "http://13.235.250.119/v2/restaurants/fetch_result/fetch_with_restaurant_id?restaurantId=$restaurantId"
                val queue=Volley.newRequestQueue(this)
                val jsonObjectRequest=object :JsonObjectRequest(Method.GET,url,null, Response.Listener {
                    val responseJsonObject=it.getJSONObject("data")
                    val success=responseJsonObject.getBoolean("success")
                    if (success){
                        restaurantMenuList.clear()
                        val data=responseJsonObject.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val menuJsonObject=data.getJSONObject(i)
                            val menuItem=RestaurantMenu(
                                menuJsonObject.getString("id"),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one")
                            )
                            restaurantMenuList.add(menuItem)
                            menuAdapter= RestaurantMenuAdapter(
                                this,
                                restaurantId,
                                restaurantName,
                                /*proceedToCartLayout,*/
                                btnProceedToCart,
                                restaurantMenuList
                            )
                            recyclerView.adapter=menuAdapter
                            recyclerView.layoutManager=layoutManager

                        }
                        restaurantMenuProgressdialog.visibility=View.GONE
                    }else{
                        Toast.makeText(this,"Some error occurred :${responseJsonObject.getString("errorMessage")}",Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this,"Volley error occurred",Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "f26a8500dd363c"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                Toast.makeText(this, e.localizedMessage?.toString(),Toast.LENGTH_SHORT).show()
            }
        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { test, listener ->
                //Code to open settings
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { test, listener ->
                //Code to exit
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }
    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        //enables the button on the tool bar
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)//change icon to custom
    }


    override fun onBackPressed() {


        if(menuAdapter.getSelectedItemCount()>0) {


            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
//                val intent=Intent(this@RestaurantMenuActivity,MainActivity::class.java)
//                startActivity(intent)
//                finish()
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }

//    override fun onNavigateUp(): Boolean {
//
//        return super.onNavigateUp()
//    }

    override fun onResume() {
        getData()
        super.onResume()
    }

}