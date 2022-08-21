package com.SigmaDating.apk.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.Postdata
import com.SigmaDating.apk.model.post
import com.SigmaDating.apk.views.userdashboard.SecondFragmentDirections
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(var context: Context) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var dataList = emptyList<Postdata>()

    internal fun setDataList(dataList: List<Postdata>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var title:TextView
        var discription:TextView
        var profile_img: CircleImageView

        init {
            profile_img= itemView.findViewById(R.id.profile_post_img)
            image = itemView.findViewById(R.id.post_img)
            title= itemView.findViewById(R.id.post_title)
            discription= itemView.findViewById(R.id.dis_title)
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        Glide.with(context).load(data.media).into(holder.image);
        holder.title.text=data.title
        holder.discription.text=data.description
        holder.image.setOnClickListener {

        }

    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}