package com.sigmadatingapp.adapters

import android.content.Context
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.sigmadatingapp.R
import com.sigmadatingapp.model.Profile
import java.util.ArrayList

class ProfileMatch(private val courseData: ArrayList<Profile>, private val context: Context, var listener: OnCategoryClickListener
) : BaseAdapter() {
    override fun getCount(): Int {
        // in get count method we are returning the size of our array list.
        return courseData.size
    }

    override fun getItem(position: Int): Any {
        // in get item method we are returning the item from our array list.
        return courseData[position]
    }

    override fun getItemId(position: Int): Long {
        // in get item id we are returning the position.
        return position.toLong()
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(position: Profile?)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // in get view method we are inflating our layout on below line.
        var v = convertView

            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_match_layout, parent, false)

        // on below line we are initializing our variables and setting data to our variables.
        // ((TextView) v.findViewById(R.id.idTVCourseName)).setText(courseData.get(position).getName());
        (v.findViewById<View>(R.id.idIVCourse) as ImageView).setImageResource(
            courseData[position].profile_img
        )
        (v.findViewById<View>(R.id.bright_img) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position]
            )
        }
        return v
    }
}