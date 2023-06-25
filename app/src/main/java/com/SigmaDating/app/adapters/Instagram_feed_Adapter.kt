package com.SigmaDating.app.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class Instagram_feed_Adapter(var context: Context, var listener: OnCategoryClickListener) :
    RecyclerView.Adapter<Instagram_feed_Adapter.ViewHolder>() {

    var dataList = emptyList<String>()

    internal fun setDataList(dataList: List<String>) {
        this.dataList = dataList
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(url :String)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var progressBar: ProgressBar

        init {
            image = itemView.findViewById(R.id.image_profile)
            progressBar = itemView.findViewById(R.id.progress_bar)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.instgram_photos, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
            holder.image.visibility = View.VISIBLE
            holder.progressBar.visibility = View.VISIBLE
            Glide.with(context).load(data).listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false;
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false;
                }

            }).into(holder.image);

        holder.image.setOnClickListener {
            Log.d("TAG@123", "setOnClickListener")
            listener.onCategoryClick(data)
        }

        }


    //  total count of items in the list
    override fun getItemCount() = dataList.size
}