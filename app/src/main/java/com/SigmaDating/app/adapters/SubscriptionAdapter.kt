package com.SigmaDating.app.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.model.Plans
import com.SigmaDating.app.model.SubscriptionPlanData
import com.SigmaDating.app.model.User_bids_list
import com.SigmaDating.app.views.Home
import com.bumptech.glide.Glide

class SubscriptionAdapter(var context: Context, private var listener: OnCategoryClickListener) :
    RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    var dataList = emptyList<Plans>()
    var color: Int = 0


    internal fun setDataList(dataList: List<Plans>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var plan_name: TextView
        var plane_price: TextView
        var plan_discription: TextView
        var main_layout: CardView
        var navigatetodis: CardView

        init {
            plan_name = itemView.findViewById(R.id.plan_name)
            plane_price = itemView.findViewById(R.id.plane_price)
            plan_discription = itemView.findViewById(R.id.plan_discription)
            main_layout = itemView.findViewById(R.id.main_layout)
            navigatetodis = itemView.findViewById(R.id.navigatetodis)

        }

    }

    interface OnCategoryClickListener {
        fun onCategoryClick(position: Plans, color: Int)
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_plans, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.plan_name.text = data.plan_name
        holder.plan_discription.text = "Per month"
        holder.plane_price.text = "$ " + data.price

        when (color) {
            0 -> {
                holder.main_layout.setCardBackgroundColor(Color.parseColor("#34488D"))
                holder.navigatetodis.setCardBackgroundColor(Color.parseColor("#EA2391"))
                data.color=color.toString()
                color = 1


            }
            1 -> {
                holder.main_layout.setCardBackgroundColor(Color.parseColor("#EA2391"))
                holder.navigatetodis.setCardBackgroundColor(Color.parseColor("#34488D"))
                data.color=color.toString()
                color = 2

            }
            2 -> {
                holder.main_layout.setCardBackgroundColor(Color.parseColor("#6C4294"))
                holder.navigatetodis.setCardBackgroundColor(Color.parseColor("#EA2391"))
                data.color=color.toString()
                color = 0

            }
            else -> {
                holder.main_layout.setCardBackgroundColor(Color.parseColor("#EA2391"))
                holder.navigatetodis.setCardBackgroundColor(Color.parseColor("#34488D"))
                data.color=color.toString()
                color = 1

            }
        }

        holder.main_layout.setOnClickListener {
            listener.onCategoryClick(data,color);
        }

        holder.navigatetodis.setOnClickListener {
            listener.onCategoryClick(data,color);
        }


    }

    override fun getItemCount() = dataList.size
    fun updateList(temp: MutableList<Plans>) {
        dataList = temp
        notifyDataSetChanged()
    }
}