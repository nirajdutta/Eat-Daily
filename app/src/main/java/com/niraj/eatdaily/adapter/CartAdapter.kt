package com.niraj.eatdaily.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niraj.eatdaily.R
import com.niraj.eatdaily.model.CartItems

class CartAdapter(val context: Context,val cartItems:ArrayList<CartItems>):RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtOrderItem:TextView=view.findViewById(R.id.txtOrderItem)
        val txtOrderItemPrice:TextView=view.findViewById(R.id.txtOrderItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int){
        val item=cartItems[position]
        holder.txtOrderItem.text=item.itemName
        holder.txtOrderItemPrice.text=item.itemPrice
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }


}
