package com.SigmaDating.app.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.model.Notification_list
import com.SigmaDating.app.model.TaggedUsers
import com.bumptech.glide.Glide

class TagsAdapter (var context: Context, private var listener: OnCategoryClickListener) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    var dataList = emptyList<TaggedUsers>()

    internal fun setDataList(dataList: List<TaggedUsers>) {
        this.dataList = dataList
        Log.d("TAG@123","Notification_list Size : "+dataList.size)
    }

    // Provide a direct reference to each of the views with data items
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name: TextView
        var maine_view :LinearLayout
        init {
            user_name=itemView.findViewById(R.id.user_name)
            maine_view=itemView.findViewById(R.id.maine_view)
        }
    }
    interface OnCategoryClickListener {
        fun onCategoryClick(position: TaggedUsers)
    }
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_tags, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data = dataList[position]
        holder.user_name.text=data.name
        holder.maine_view.setOnClickListener {
            listener.onCategoryClick(data);
        }

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}