package com.niraj.eatdaily.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.R
import com.niraj.eatdaily.adapter.HomeRecyclerAdapter
import com.niraj.eatdaily.database.RestaurantDatabase
import com.niraj.eatdaily.database.RestaurantEntity
import com.niraj.eatdaily.model.Restaurant
import com.niraj.eatdaily.util.ConnectionManager

class FavoritesFragment : Fragment() {
    lateinit var recyclerFavorite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favoriteRecyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var favoriteRestaurantsList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        layoutManager = LinearLayoutManager(activity as Context)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        return view
    }

    fun fetchData() {
        val queue = Volley.newRequestQueue(context as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        favoriteRestaurantsList.clear()
                        progressLayout.visibility = View.GONE
                        val jsonArray = data.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val restaurantJsonObject = jsonArray.getJSONObject(i)
                            val restaurantEntity = RestaurantEntity(
                                restaurantJsonObject.getString("id").toInt(),
                                restaurantJsonObject.getString("name")
                            )
                            if (DBAsyncTask(activity as Context, restaurantEntity, 1).execute()
                                    .get()
                            ) {

                                val restaurant = Restaurant(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                favoriteRestaurantsList.add(restaurant)

                                favoriteRecyclerAdapter =
                                    HomeRecyclerAdapter(
                                        activity as Context,
                                        favoriteRestaurantsList
                                    )
                                recyclerFavorite.adapter = favoriteRecyclerAdapter
                                recyclerFavorite.layoutManager = layoutManager
                            }
                        }

                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }, Response.ErrorListener {
                    //error
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred !",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "f26a8500dd363c"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { test, listener ->
                //Code to open settings
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { test, listener ->
                //Code to exit
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(
                context.applicationContext,
                RestaurantDatabase::class.java,
                "restaurant-db"
            ).build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    //Check DB if the restaurant is favourite or not
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id)
                    db.close()
//                    println("restaurantValue:$restaurant")
                    return restaurant != null
                }
                else -> return false
            }
        }
    }

    override fun onResume() {
        fetchData()
        super.onResume()
    }
}