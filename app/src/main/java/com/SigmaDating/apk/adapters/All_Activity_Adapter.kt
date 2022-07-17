package com.SigmaDating.apk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata
import com.bumptech.glide.Glide

class All_Activity_Adapter(var context: Context, private var listener: OnCategoryClickListener) : RecyclerView.Adapter<All_Activity_Adapter.ViewHolder>() {

    var dataList = emptyList<EditProfiledata>()

    internal fun setDataList(dataList: List<EditProfiledata>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.image)
        }

    }
    interface OnCategoryClickListener {
        fun onCategoryClick(position: EditProfiledata)
    }
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        Glide.with(context).load(data.icon).into(holder.image);

        holder.image.setOnClickListener {
            listener!!.onCategoryClick(data);
        }
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}