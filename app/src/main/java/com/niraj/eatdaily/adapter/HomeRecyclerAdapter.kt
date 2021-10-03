package com.niraj.eatdaily.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.niraj.eatdaily.R
import com.niraj.eatdaily.activity.MainActivity
import com.niraj.eatdaily.activity.RestaurantMenuActivity
import com.niraj.eatdaily.database.RestaurantDatabase
import com.niraj.eatdaily.database.RestaurantEntity
import com.niraj.eatdaily.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, private var restaurantList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPersonPrice: TextView = view.findViewById(R.id.txtPersonPrice)
        val txtAddToFav: TextView = view.findViewById(R.id.txtAddToFav)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgDish: ImageView = view.findViewById(R.id.imgDish)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        val restaurantEntity = RestaurantEntity(
            restaurant.id,
            restaurant.name/*,restaurant.rating,restaurant.cost_for_one,restaurant.image*/
        )
        holder.txtRestaurantName.tag="${restaurant.id}"
        holder.txtRestaurantName.text = restaurant.name
        holder.txtPersonPrice.text = restaurant.cost_for_one
        val imgUrl = restaurant.image
        Picasso.get().load(imgUrl).error(R.drawable.hamburger_icon).into(holder.imgDish)
        holder.txtRating.text = restaurant.rating

        val isFav = DBAsyncTask(context as Context, restaurantEntity, 1).execute().get()
//        println("isFav= $isFav")
        if (isFav) {
            holder.txtAddToFav.tag = "liked"
            holder.txtAddToFav.background =
                context.resources.getDrawable(R.drawable.ic_fav_filled)

        } else {
            holder.txtAddToFav.tag = "unliked"
            holder.txtAddToFav.background =
                context.resources.getDrawable(R.drawable.ic_fav_outline)
        }

        holder.txtAddToFav.setOnClickListener {

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()
                if (result) {
                    Toast.makeText(context, "Restaurant Added to Favorites", Toast.LENGTH_SHORT)
                        .show()
                    holder.txtAddToFav.tag = "liked"
                    holder.txtAddToFav.background =
                        context.resources.getDrawable(R.drawable.ic_fav_filled)
                } else {
                    Toast.makeText(context, "Cannot add to Favorites", Toast.LENGTH_SHORT).show()
                }
            } else {
                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant has been removed from Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.txtAddToFav.tag = "unliked"
                    holder.txtAddToFav.background =
                        context.resources.getDrawable(R.drawable.ic_fav_outline)
                } else {
                    Toast.makeText(context, "Cannot Remove from Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        holder.llContent.setOnClickListener {
            val intent=Intent(context,RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId",holder.txtRestaurantName.tag.toString())
            intent.putExtra("restaurantName",holder.txtRestaurantName.text.toString())
            context.startActivity(intent)
        }
    }

    fun filterList(filteredList: ArrayList<Restaurant>) {//to update the recycler view depending on the search
        restaurantList = filteredList
        notifyDataSetChanged()
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

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
                2 -> {
                    //add restaurant to favorite

                    db.restaurantDao().addRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
                3 -> {
                    //delete restaurant from favorite
                    db.restaurantDao().removeRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false
            }
        }
    }
}