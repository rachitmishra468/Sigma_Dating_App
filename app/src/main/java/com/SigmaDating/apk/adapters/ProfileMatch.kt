package com.SigmaDating.apk.adapters

import android.content.Context
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.SigmaDating.R
import com.SigmaDating.apk.model.Bids
import com.SigmaDating.apk.model.Profile
import com.bumptech.glide.Glide
import java.util.ArrayList

class ProfileMatch(private val courseData: ArrayList<Bids>, private val context: Context, var listener: OnCategoryClickListener
) : BaseAdapter() {
    override fun getCount(): Int {
        return courseData.size
    }

    override fun getItem(position: Int): Any {
        return courseData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(position: Bids?,count :Int)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
            v = LayoutInflater.from(parent.context).inflate(R.layout.profile_match_layout, parent, false)

       var mageview= (v.findViewById<View>(R.id.idIVCourse) as ImageView)
        Glide.with(context).load(courseData[position].upload_image).into(mageview);

        (v.findViewById<View>(R.id.tv_username) as TextView).setText(courseData[position].first_name
        +""+courseData[position].last_name)

        (v.findViewById<View>(R.id.bright_img) as ImageView).setOnClickListener {
            listener.onCategoryClick(courseData[position],1//done
            )
        }

        (v.findViewById<View>(R.id.star_view) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position],2
            )
        }

        (v.findViewById<View>(R.id.super_like) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position],3
            )
        }

        (v.findViewById<View>(R.id.grid_view) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position],4
            )
        }

        (v.findViewById<View>(R.id.cancle_view) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position],5
            )
        }

        (v.findViewById<View>(R.id.idIVCourse) as ImageView).setOnClickListener {

        }




        return v
    }
}