package com.niraj.eatdaily.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.niraj.eatdaily.util.ConnectionManager
import com.niraj.eatdaily.adapter.HomeRecyclerAdapter
import com.niraj.eatdaily.R
import com.niraj.eatdaily.activity.MainActivity
import com.niraj.eatdaily.model.Restaurant
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment(val contextParam:Context) : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var radioButtonView: View
    lateinit var editTextSearch: EditText
    private lateinit var cantFindRestaurantLayout:RelativeLayout

    var restaurantList = arrayListOf<Restaurant>()

    var ratingComparator= Comparator<Restaurant> { rest1, rest2 ->

        if(rest1.rating.compareTo(rest2.rating,true)==0){
            rest1.name.compareTo(rest2.name,true)
        }
        else{
            rest1.rating.compareTo(rest2.rating,true)
        }

    }

    var costComparator= Comparator<Restaurant> { rest1, rest2 ->

        rest1.cost_for_one.compareTo(rest2.cost_for_one,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity as Context)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        editTextSearch=view.findViewById(R.id.editTextSearch)
        cantFindRestaurantLayout=view.findViewById(R.id.dashboard_fragment_cant_find_restaurant)

        progressLayout.visibility=View.VISIBLE
        progressBar.visibility=View.VISIBLE
        homeRecyclerAdapter= HomeRecyclerAdapter(activity as Context,restaurantList)

        val queue = Volley.newRequestQueue(context as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(contextParam as Context)) {
            try {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            restaurantList.clear()
                            progressLayout.visibility=View.GONE
                            val jsonArray = data.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val restaurantJsonObject = jsonArray.getJSONObject(i)
                                val restaurant = Restaurant(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurant)

                                homeRecyclerAdapter =
                                    HomeRecyclerAdapter(contextParam, restaurantList)
                                recyclerHome.adapter = homeRecyclerAdapter
                                recyclerHome.layoutManager = layoutManager
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
            }catch (e:JSONException){
                Toast.makeText(
                    activity as Context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

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

        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<Restaurant>()

            for (item in restaurantList){
                if(item.name.toLowerCase(Locale.ROOT).contains(strTyped.toLowerCase(Locale.ROOT))) {//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            if(filteredList.size==0){
                cantFindRestaurantLayout.visibility=View.VISIBLE
            }
            else{
                cantFindRestaurantLayout.visibility=View.INVISIBLE
            }

            homeRecyclerAdapter.filterList(filteredList)

        }

        editTextSearch.addTextChangedListener(object : TextWatcher {//as the user types the search filter is applied
        override fun afterTextChanged(strTyped: Editable?) {
            filterFun(strTyped.toString())
        }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )

        return view
    }
    fun fetchData(){
        val queue = Volley.newRequestQueue(context as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity!! as Context)) {
            try {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            restaurantList.clear()
                            progressLayout.visibility=View.GONE
                            val jsonArray = data.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val restaurantJsonObject = jsonArray.getJSONObject(i)
                                val restaurant = Restaurant(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurant)

                                homeRecyclerAdapter =
                                    HomeRecyclerAdapter(activity!! as Context, restaurantList)
                                recyclerHome.adapter = homeRecyclerAdapter
                                recyclerHome.layoutManager = layoutManager
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
            }catch (e:JSONException){
                Toast.makeText(
                    activity as Context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){

            R.id.sort->{
                radioButtonView= View.inflate(activity as Context,R.layout.sort_radio_button,null)//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(restaurantList, costComparator)
                            restaurantList.reverse()
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantList, costComparator)
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort,menu)
    }

    override fun onResume() {
        fetchData()
        super.onResume()
    }
}