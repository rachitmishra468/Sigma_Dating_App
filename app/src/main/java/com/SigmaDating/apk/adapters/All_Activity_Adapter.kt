package com.SigmaDating.apk.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.model.Notification_list
import com.bumptech.glide.Glide

class All_Activity_Adapter(var context: Context, private var listener: OnCategoryClickListener) : RecyclerView.Adapter<All_Activity_Adapter.ViewHolder>() {

    var dataList = emptyList<Notification_list>()

    internal fun setDataList(dataList: List<Notification_list>) {
        this.dataList = dataList
        Log.d("TAG@123","Notification_list Size : "+dataList.size)
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var delete_notification:ImageView
        var notification_title:TextView
        var notification_message:TextView

        init {
            image = itemView.findViewById(R.id.image)
            delete_notification=itemView.findViewById(R.id.delete_notification)
            notification_title=itemView.findViewById(R.id.notification_title)
            notification_message=itemView.findViewById(R.id.notification_message)
        }

    }
    interface OnCategoryClickListener {
        fun onCategoryClick(position: Notification_list)
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


        holder.notification_title.text=data.title
        holder.notification_message.text=data.description

        holder.delete_notification.setOnClickListener {
            listener!!.onCategoryClick(data);
        }
        Glide.with(context).load(data.upload_image).into(holder.image);

        holder.image.setOnClickListener {

        }
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}