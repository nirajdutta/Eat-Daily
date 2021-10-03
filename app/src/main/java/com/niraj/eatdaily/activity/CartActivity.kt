package com.niraj.eatdaily.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.adapter.CartAdapter
import com.niraj.eatdaily.model.CartItems
import com.niraj.eatdaily.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var txtOrderingFrom: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartAdapter: CartAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var linearLayout: LinearLayout
    lateinit var activity_cart_Progressdialog: RelativeLayout
    lateinit var selectedItemsId: ArrayList<String>

    var cartItemList = arrayListOf<CartItems>()
    var totalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolBar)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        recyclerView = findViewById(R.id.recyclerViewCart)
        layoutManager = LinearLayoutManager(this)
        linearLayout = findViewById(R.id.linearLayout)
        activity_cart_Progressdialog = findViewById(R.id.activity_cart_Progressdialog)
        activity_cart_Progressdialog.visibility = View.VISIBLE



        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId") as ArrayList<String>

        txtOrderingFrom.text = restaurantName
        setToolBar()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (ConnectionManager().checkConnectivity(this)) {
            try {

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restaurantId}"
                val jsonObjectRequest = @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")
                            cartItemList.clear()

                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (selectedItemsId.contains(cartItemJsonObject.getString("id")))//if the fetched id is present in the selected id save
                                {

                                    val menuObject = CartItems(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()
                                    cartItemList.add(menuObject)
                                }
                                cartAdapter = CartAdapter(
                                    this,
                                    cartItemList
                                )
                                recyclerView.adapter =
                                    cartAdapter//bind the  recyclerView to the adapter

                                recyclerView.layoutManager =
                                    layoutManager //bind the  recyclerView to the layoutManager

                            }

                            //set the total on the button
                            btnPlaceOrder.text = "Place Order(Total:Rs. $totalAmount)"

                        }

                        activity_cart_Progressdialog.visibility = View.GONE

                        btnPlaceOrder.setOnClickListener {

                            val sharedPreferencess = this.getSharedPreferences(
                                getString(R.string.preference_file_name),
                                Context.MODE_PRIVATE
                            )

                            activity_cart_Progressdialog.visibility = View.VISIBLE

                            val foodJsonArray = JSONArray()

                            for (foodItem in selectedItemsId) {
                                val singleItemObject = JSONObject()
                                singleItemObject.put("food_item_id", foodItem)
                                foodJsonArray.put(singleItemObject)

                            }

                            val sendOrder = JSONObject()

                            sendOrder.put("user_id", sharedPreferencess.getString("user_id", "0"))
                            sendOrder.put("restaurant_id", restaurantId.toString())
                            sendOrder.put("total_cost", totalAmount)
                            sendOrder.put("food", foodJsonArray)

                            val queueOrder = Volley.newRequestQueue(this)

                            val orderUrl = "http://13.235.250.119/v2/place_order/fetch_result"

                            val jsonObjectRequest1 = object : JsonObjectRequest(
                                Method.POST,
                                orderUrl,
                                sendOrder,
                                Response.Listener {

                                    val responseJsonObjectData1 = it.getJSONObject("data")

                                    val success1 = responseJsonObjectData1.getBoolean("success")

                                    if (success1) {

                                        Toast.makeText(
                                            this,
                                            "Order Placed",
                                            Toast.LENGTH_SHORT
                                        ).show()


                                                val intent= Intent(this,OrderPlacedActivity::class.java)

                                                startActivity(intent)

                                        finishAffinity()//destory all previous activities


                                    } else {
                                        val responseMessageServer =
                                            responseJsonObjectData.getString("errorMessage")
                                        Toast.makeText(
                                            this,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    activity_cart_Progressdialog.visibility = View.INVISIBLE
                                },
                                Response.ErrorListener {

                                    println("ssssss" + it)

                                    Toast.makeText(
                                        this,
                                        "Some Error occurred!!!",
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
                            jsonObjectRequest1.retryPolicy = DefaultRetryPolicy(
                                15000,
                                1,
                                1f
                            )

                            queueOrder.add(jsonObjectRequest1)

                        }


                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        activity_cart_Progressdialog.visibility = View.INVISIBLE

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
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
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

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)//change icon to custom
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        val id = item.itemId
//
//        when (id) {
//            android.R.id.home -> {
//                super.onBackPressed()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onBackPressed() {
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
        Toast.makeText(this,"Order cancelled",Toast.LENGTH_SHORT).show()
    }

}