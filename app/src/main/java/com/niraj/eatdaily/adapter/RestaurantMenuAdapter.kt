package com.niraj.eatdaily.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niraj.eatdaily.R
import com.niraj.eatdaily.activity.CartActivity
import com.niraj.eatdaily.model.RestaurantMenu

class RestaurantMenuAdapter(
    val context: Context,
    private val restaurantId: String,
    private val restaurantName: String,
//    private val proceedToCartPassed: RelativeLayout,
    private val btnProceedToCart: Button,
    private val restaurantMenuList: ArrayList<RestaurantMenu>
) : RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {

    var itemSelectedCount: Int = 0
//    lateinit var proceedToCart: RelativeLayout
    var itemsSelectedId = arrayListOf<String>()

    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_menu_single_row, parent, false)
        return ViewHolderRestaurantMenu(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenu = restaurantMenuList[position]
        holder.btnAddToCart.tag=restaurantMenu.id.toString()
        holder.txtSerialNumber.text=(position+1).toString()
        holder.txtItemName.text=restaurantMenu.name
        holder.txtItemPrice.text="Rs."+restaurantMenu.cost_for_one
//        proceedToCart = proceedToCartPassed
        holder.btnAddToCart.setOnClickListener {
            
            if(holder.btnAddToCart.text.toString() == "Remove")
            {
                itemSelectedCount--//unselected

                itemsSelectedId.remove(holder.btnAddToCart.tag.toString())

                holder.btnAddToCart.text="Add"

                holder.btnAddToCart.setBackgroundColor(Color.rgb(244, 67, 54))//primary colour to rgb

            }
            else
            {
                itemSelectedCount++//selected

                itemsSelectedId.add(holder.btnAddToCart.tag.toString())


                holder.btnAddToCart.text="Remove"

                holder.btnAddToCart.setBackgroundColor(Color.rgb(255,196,0))//yellow colour to rgb

            }

            if(itemSelectedCount>0){
                btnProceedToCart.visibility=View.VISIBLE
            }
            else{
                btnProceedToCart.visibility=View.INVISIBLE
            }
        }



        btnProceedToCart.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, CartActivity::class.java)

            intent.putExtra(
                "restaurantId",
                restaurantId.toString()
            )// pass the restaurant id to the next activity

            intent.putExtra("restaurantName", restaurantName)

            intent.putExtra(
                "selectedItemsId",
                itemsSelectedId
            )//passing all the items selected by the user

            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return restaurantMenuList.size
    }
    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }
}