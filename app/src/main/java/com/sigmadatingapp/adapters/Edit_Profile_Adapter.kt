package com.sigmadatingapp.adapters
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sigmadatingapp.R
import com.sigmadatingapp.model.EditProfiledata

class Edit_Profile_Adapter(var context: Context,var listener : OnCategoryClickListener) :
    RecyclerView.Adapter<Edit_Profile_Adapter.ViewHolder>() {

    var dataList = emptyList<String>()

    internal fun setDataList(dataList: List<String>) {
        this.dataList = dataList
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(position: Int,boolean: Boolean)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var camera_img:ImageView
        var delete_img:ImageView

        init {
            image = itemView.findViewById(R.id.image_profile)
            camera_img= itemView.findViewById(R.id.camera_img)
            delete_img= itemView.findViewById(R.id.delete_img)
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
        if(data.equals("ADD_IMAGES")){
            holder.camera_img.visibility=View.VISIBLE
            holder.image.visibility=View.INVISIBLE
            holder.delete_img.visibility=View.INVISIBLE

        }
        else{
            holder.camera_img.visibility=View.GONE
            holder.image.visibility=View.VISIBLE
            holder.delete_img.visibility=View.VISIBLE
            Glide.with(context).load(data).into(holder.image);
        }


        holder.camera_img.setOnClickListener {
            Log.d("TAG@123","setOnClickListener")
            listener.onCategoryClick(position,true)
        }

        holder.delete_img.setOnClickListener {
            Log.d("TAG@123","setOnClickListener")
            listener.onCategoryClick(position,false)
        }




    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}