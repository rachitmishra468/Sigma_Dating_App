package com.SigmaDating.app.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.model.Postdata
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class PostAdapter(
    var booleantype: Boolean,
    private var listener: PostAdapter.OnItemClickListener,
    var context: Context
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var dataList = emptyList<Postdata>()
    internal fun setDataList(dataList: List<Postdata>) {
        this.dataList = ArrayList<Postdata>()
        this.dataList = dataList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onDelete(position: Postdata, flag: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var title: TextView
        var commetest_name:TextView
        var location_text:TextView
        var discription: TextView
        var profile_img: CircleImageView
        var progressBar: ProgressBar
        var postDelet: ImageView
        var comment_img: ImageView
        var img_like: ImageView
        var post_visility:ImageView
        var videoView: PlayerView

        init {
            location_text= itemView.findViewById(R.id.location_text)
            commetest_name= itemView.findViewById(R.id.commetest_name)
            profile_img = itemView.findViewById(R.id.profile_post_img)
            image = itemView.findViewById(R.id.post_img)
            title = itemView.findViewById(R.id.post_title)
            discription = itemView.findViewById(R.id.dis_title)
            progressBar = itemView.findViewById(R.id.progress_post)
            comment_img = itemView.findViewById(R.id.comment_img)
            postDelet = itemView.findViewById(R.id.post_delete_img)
            img_like = itemView.findViewById(R.id.img_like)
            videoView = itemView.findViewById(R.id.videoView)
            post_visility=itemView.findViewById(R.id.post_visility_img)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.title.text = data.title
        holder.discription.text = data.description
        holder.commetest_name.text=data.first_name+" "+data.last_name
        holder.location_text.text=data.location
        Glide.with(context).load(data.upload_image).into(holder.profile_img);
        if (data.like) {
            holder.img_like.setImageDrawable(context.resources.getDrawable(R.drawable.heart_solid))
        } else {
            holder.img_like.setImageDrawable(context.resources.getDrawable(R.drawable.white_heart))
        }

        if (data.isPrivate) {
            holder.post_visility.setImageDrawable(context.resources.getDrawable(R.drawable.visibility_off_post))
        } else {
            holder.post_visility.setImageDrawable(context.resources.getDrawable(R.drawable.visibility_post))
        }




        if (!data.videofile.isNullOrEmpty()) {
            Log.d("TAG@123", "video/mp4")
            holder.videoView.visibility = View.VISIBLE
            holder.image.visibility = View.INVISIBLE
            holder.progressBar.visibility = View.GONE
            var mPlayer = SimpleExoPlayer.Builder(context).build()
            holder.videoView.player = mPlayer
            mPlayer.playWhenReady = false
            mPlayer.setMediaSource(buildMediaSource(data.videofile))
            mPlayer.prepare()
            mPlayer.pause()
        } else {
            holder.videoView.visibility = View.INVISIBLE
            holder.image.visibility = View.VISIBLE
            Glide.with(context).load(data.media).listener(object : RequestListener<Drawable> {
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


        }
        holder.img_like.setOnClickListener {
            holder.img_like.setImageDrawable(context.resources.getDrawable(R.drawable.heart_solid))
            data.like = true
            listener.onDelete(data, 2)
        }

        holder.post_visility.setOnClickListener {
            if (data.isPrivate) {
                holder.post_visility.setImageDrawable(context.resources.getDrawable(R.drawable.visibility_off_post))
                data.isPrivate=false
                listener.onDelete(data, 4)
            } else {
                data.isPrivate=true
                listener.onDelete(data, 5)
                holder.post_visility.setImageDrawable(context.resources.getDrawable(R.drawable.visibility_post))
            }
        }


        holder.comment_img.setOnClickListener {
            listener.onDelete(data, 1)
        }


        if (!booleantype) {
            holder.postDelet.visibility = View.GONE
            holder.post_visility.visibility = View.GONE
        } else {
            holder.postDelet.visibility = View.VISIBLE
            holder.post_visility.visibility = View.VISIBLE
        }
        holder.postDelet.setOnClickListener { listener.onDelete(data, 3) }

    }


    private fun buildMediaSource(videoURL: String): MediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))
        return mediaSource
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}