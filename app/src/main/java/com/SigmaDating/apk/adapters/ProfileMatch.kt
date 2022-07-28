package com.SigmaDating.apk.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.SigmaDating.R
import com.SigmaDating.apk.model.Bids
import com.SigmaDating.apk.views.OnSwipeTouchListener
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
        fun onCategoryClick(position: Bids?, count: Int, extras: FragmentNavigator.Extras?, imageView: ImageView)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
            v = LayoutInflater.from(parent.context).inflate(R.layout.profile_match_layout, parent, false)

       var mageview= (v.findViewById<View>(R.id.idIVCourse) as ImageView)
        mageview.apply {
            transitionName = courseData[position].upload_image
        }

        Glide.with(context).load(courseData[position].upload_image).into(mageview);

        mageview.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()

                listener.onCategoryClick(
                    courseData[position],5,null, mageview
                )

            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                listener.onCategoryClick(
                    courseData[position],2,null,mageview
                )
            }
            @SuppressLint("ClickableViewAccessibility")
            override fun onSwipeUp() {
                super.onSwipeUp()
                listener.onCategoryClick(
                    courseData[position],3,null,mageview
                )
            }
            override fun onSwipeDown() {
                super.onSwipeDown()

            }
        })







        (v.findViewById<View>(R.id.tv_username) as TextView).setText(courseData[position].first_name
        +""+courseData[position].last_name)

        (v.findViewById<View>(R.id.bright_img) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position], 1, null,mageview//done
            )
        }

        (v.findViewById<View>(R.id.star_view) as ImageView).setOnClickListener {
           // val extras = FragmentNavigatorExtras(mageview )
            listener.onCategoryClick(

                courseData[position],2,null
           ,mageview )
        }

        (v.findViewById<View>(R.id.super_like) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position], 3, null
         ,mageview   )
        }

        (v.findViewById<View>(R.id.grid_view) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position], 4, null
            ,mageview)
        }

        (v.findViewById<View>(R.id.cancle_view) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position], 5, null
            ,mageview)
        }

        (v.findViewById<View>(R.id.idIVCourse) as ImageView).setOnClickListener {

        }




        return v
    }
}