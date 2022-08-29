package com.SigmaDating.apk.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.Postdata
import com.SigmaDating.apk.model.comment_list
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.RequestListener
import de.hdodenhof.circleimageview.CircleImageView

class Comment_Adapter ( var booleantype: Boolean,var context: Context) : RecyclerView.Adapter<Comment_Adapter.ViewHolder>() {

    var dataList = emptyList<comment_list>()
    internal fun setDataList(dataList: List<comment_list>) {
        this.dataList=ArrayList<comment_list>()
        this.dataList = dataList
        notifyDataSetChanged()
    }

    // Provide a direct reference to each of the views with data items
    interface OnItemClickListener {
        fun onDelete(position: Postdata)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var commentuser_name: TextView
        var comment_comment: TextView

        var comment_user_profile: CircleImageView


        init {
            comment_user_profile= itemView.findViewById(R.id.comment_user_profile)
            commentuser_name= itemView.findViewById(R.id.commentuser_name)
            comment_comment= itemView.findViewById(R.id.comment_comment)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        val data = dataList[position]
        holder.commentuser_name.text=data.first_name+" "+data.last_name
        holder.comment_comment.text=data.comment_text
        Glide.with(context).load(data.upload_image).into(holder.comment_user_profile);



    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}