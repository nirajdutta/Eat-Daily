package com.niraj.eatdaily.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="restaurants")//will tell the complier that it is an entity class
data class RestaurantEntity(
    @PrimaryKey val restaurant_id: Int,
    @ColumnInfo(name="restaurant_name") val restaurantName:String,
//    @ColumnInfo(name="restaurant_rating") val rating:String,
//    @ColumnInfo(name="restaurant_cost") val cost_for_one:String,
//    @ColumnInfo(name="restaurant_image")val image:String
)