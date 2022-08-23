package com.SigmaDating.apk.adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.model.Postdata
import com.SigmaDating.apk.views.FirstFragmentDirections
import com.SigmaDating.apk.views.userdashboard.SecondFragmentDirections


class Profile_Adapter(var context: Context) : RecyclerView.Adapter<Profile_Adapter.ViewHolder>() {

    var dataList = emptyList<Postdata>()

    internal fun setDataList(dataList: List<Postdata>) {
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

        var data = dataList[position]
        holder.image.apply {
            transitionName = data.media
        }
        Glide.with(context).load(data.media).into(holder.image);

        holder.image.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id",data.user_id)
            holder.image.findNavController().navigate(R.id.action_SecondFragment_to_postlist,bundle)
        }

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}