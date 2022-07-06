package com.sigmadatingapp.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sigmadatingapp.R
import com.sigmadatingapp.model.EditProfiledata

class Edit_Profile_Adapter(var context: Context) :
    RecyclerView.Adapter<Edit_Profile_Adapter.ViewHolder>() {

    var dataList = emptyList<EditProfiledata>()

    internal fun setDataList(dataList: List<EditProfiledata>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.image_profile)
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Edit_Profile_Adapter.ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.update_photo, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: Edit_Profile_Adapter.ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        Glide.with(context).load(data.icon).into(holder.image);

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}