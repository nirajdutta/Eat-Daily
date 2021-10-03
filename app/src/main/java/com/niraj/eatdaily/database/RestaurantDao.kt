package com.niraj.eatdaily.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun addRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun removeRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants():List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE restaurant_id is :restaurant_id")
    fun getRestaurantById(restaurant_id: Int):RestaurantEntity
}