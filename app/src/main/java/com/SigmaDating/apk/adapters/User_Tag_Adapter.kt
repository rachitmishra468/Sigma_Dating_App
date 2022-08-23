package com.SigmaDating.apk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.User_bids_list
import com.bumptech.glide.Glide

class User_Tag_Adapter (var context: Context, private var listener: OnCategoryClickListener) :
    RecyclerView.Adapter<User_Tag_Adapter.ViewHolder>() {

    var dataList = emptyList<User_bids_list>()

    internal fun setDataList(dataList: List<User_bids_list>) {
        this.dataList = dataList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var textname: TextView
        var univercity_name: TextView
        var date_text: TextView
        var back_tag_user:LinearLayout

        init {
            image = itemView.findViewById(R.id.image)
            textname = itemView.findViewById(R.id.chat_text_name)
            univercity_name = itemView.findViewById(R.id.univercity_name)
            date_text= itemView.findViewById(R.id.date_text)
            back_tag_user= itemView.findViewById(R.id.back_tag_user)

        }

    }

    interface OnCategoryClickListener {
        fun onCategoryClick(position: User_bids_list)
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_tag_item_data, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        val data = dataList[position]

        // Set item views based on your views and data model
        Glide.with(context).load(data.upload_image).into(holder.image);
        if(!data.tag_add){
            holder.back_tag_user.setBackgroundColor(context.getResources().getColor(R.color.white))
        }
        else{
            holder.back_tag_user.setBackgroundColor(context.getResources().getColor(R.color.blue))
        }

        holder.image.setOnClickListener {
            holder.back_tag_user.setBackgroundColor(context.getResources().getColor(R.color.blue))
            listener.onCategoryClick(data);
        }

        holder.textname.text = data.first_name + " " + data.last_name
        holder.univercity_name.text = data.university
        holder.date_text.text = data.dob


    }

    override fun getItemCount() = dataList.size
    fun updateList(temp: MutableList<User_bids_list>) {
        dataList = temp
        notifyDataSetChanged()
    }
}