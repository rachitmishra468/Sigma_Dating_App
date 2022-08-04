package com.SigmaDating.apk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata

class ChatList_Adapter(var context: Context,private var listener: OnCategoryClickListener) : RecyclerView.Adapter<ChatList_Adapter.ViewHolder>() {

    var dataList = emptyList<EditProfiledata>()

    internal fun setDataList(dataList: List<EditProfiledata>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
var textname:TextView
        init {
            image = itemView.findViewById(R.id.image)
            textname=itemView.findViewById(R.id.chat_text_name)

        }

    }
    interface OnCategoryClickListener {
        fun onCategoryClick(position: EditProfiledata)
    }
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        val data = dataList[position]

        // Set item views based on your views and data model
        Glide.with(context).load(data.icon).into(holder.image);

        holder.image.setOnClickListener {
            listener!!.onCategoryClick(data);
        }
        holder.textname.text=data.text

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
    fun updateList(temp: MutableList<EditProfiledata>) {
dataList=temp
        notifyDataSetChanged()
    }
}