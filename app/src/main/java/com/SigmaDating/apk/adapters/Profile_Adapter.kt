package com.SigmaDating.apk.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.views.FirstFragmentDirections
import com.SigmaDating.apk.views.userdashboard.SecondFragmentDirections


class Profile_Adapter(var context: Context) : RecyclerView.Adapter<Profile_Adapter.ViewHolder>() {

    var dataList = emptyList<String>()

    internal fun setDataList(dataList: List<String>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.image)
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]
        holder.image.apply {
            transitionName = data.trim()
        }
        // Set item views based on your views and data model
        Glide.with(context).load(data.trim()).into(holder.image);

        holder.image.setOnClickListener {
            val extrass = FragmentNavigatorExtras(holder.image to data.trim())
            val action = SecondFragmentDirections.actionSecondFragmentToFullScreenFragment(
                fullImage =data.trim()
            )
            holder.image.findNavController().navigate(action,extrass)
        }

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}