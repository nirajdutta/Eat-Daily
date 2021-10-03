package com.niraj.eatdaily.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.adapter.OrderHistoryAdapter
import com.niraj.eatdaily.model.OrderHistoryRestaurant
import com.niraj.eatdaily.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {
    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var menuAdapter1: OrderHistoryAdapter
    lateinit var recyclerViewAllOrders: RecyclerView
    lateinit var order_activity_history_Progressdialog: RelativeLayout
    lateinit var order_history_fragment_no_orders: RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerViewAllOrders = view.findViewById(R.id.recyclerViewAllOrders)

        order_activity_history_Progressdialog =
            view.findViewById(R.id.order_activity_history_Progressdialog)

        order_history_fragment_no_orders = view.findViewById(R.id.order_history_fragment_no_orders)

        layoutManager1 = LinearLayoutManager(activity as Context)

        val orderedRestaurantList = ArrayList<OrderHistoryRestaurant>()

        val sharedPreferencess = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        val user_id = sharedPreferencess?.getString("user_id", "000")

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            order_activity_history_Progressdialog.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(context as Context)

                val url = "http://13.235.250.119/v2/orders/fetch_result?userId=$user_id"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            if (data.length() == 0) {//no items present display toast

                                Toast.makeText(
                                    activity,
                                    "No Orders Placed yet!!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                order_history_fragment_no_orders.visibility = View.VISIBLE

                            } else {
                                order_history_fragment_no_orders.visibility = View.INVISIBLE



                                for (i in 0 until data.length()) {
                                    val restaurantItemJsonObject = data.getJSONObject(i)

                                    val eachRestaurantObject = OrderHistoryRestaurant(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at")
                                            .substring(0, 10)
                                    )// only date is extracted

                                    orderedRestaurantList.add(eachRestaurantObject)

                                    menuAdapter1 = OrderHistoryAdapter(
                                        activity as Context,
                                        orderedRestaurantList
                                    )//set the adapter with the data

                                    recyclerViewAllOrders.adapter =
                                        menuAdapter1//bind the  recyclerView to the adapter

                                    recyclerViewAllOrders.layoutManager =
                                        layoutManager1 //bind the  recyclerView to the layoutManager

                                }

                            }

                        }
                        order_activity_history_Progressdialog.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        order_activity_history_Progressdialog.visibility = View.INVISIBLE

                        Toast.makeText(
                            activity,
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

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    activity as Context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Toast.makeText(
                activity as Context,
                "No Internet Found",
                Toast.LENGTH_SHORT
            ).show()

        }

        return view
    }
}