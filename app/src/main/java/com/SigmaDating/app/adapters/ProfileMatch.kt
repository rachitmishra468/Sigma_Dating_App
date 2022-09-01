package com.SigmaDating.app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.FragmentNavigator
import com.SigmaDating.R
import com.SigmaDating.app.model.Bids
import com.SigmaDating.app.views.OnSwipeTouchListener
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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
        var idIV_actiontyp=(v.findViewById<LottieAnimationView>(R.id.img_hide))
        var progressBar = (v.findViewById<ProgressBar>(R.id.progress_bar))
        var greek_latter=(v.findViewById<TextView>(R.id.greek_latter))
        var broken_heart=(v.findViewById<LottieAnimationView>(R.id.broken_heart))
        var heart_loading=(v.findViewById<LottieAnimationView>(R.id.heart_loading))

        mageview.apply {
            transitionName = courseData[position].upload_image
        }

        progressBar.visibility = View.VISIBLE
        Glide.with(context).load(courseData[position].upload_image).listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false;
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false;
            }

        }).into(mageview);





        mageview.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
               /* broken_heart.setVisibility(View.VISIBLE)
                broken_heart.playAnimation()
                Handler().postDelayed(
                    java.lang.Runnable {
                        broken_heart.setVisibility(View.GONE)

                        listener.onCategoryClick(
                            courseData[position],5,null, mageview
                        )
                    },
                    1200
                )*/


                listener.onCategoryClick(
                    courseData[position],5,null, mageview
                )




            }
            override fun onSwipeRight() {
                super.onSwipeRight()

               /* heart_loading.setVisibility(View.VISIBLE)
                heart_loading.playAnimation()
                Handler().postDelayed(
                    java.lang.Runnable {
                        heart_loading.setVisibility(View.GONE)

                        listener.onCategoryClick(
                            courseData[position],2,null,mageview
                        )
                    },
                    1200
                )*/

                listener.onCategoryClick(
                    courseData[position],2,null,mageview
                )


            }
            @SuppressLint("ClickableViewAccessibility")
            override fun onSwipeUp() {
                super.onSwipeUp()

               /* idIV_actiontyp.setVisibility(View.VISIBLE)
                idIV_actiontyp.playAnimation()
                Handler().postDelayed(
                    java.lang.Runnable {
                        idIV_actiontyp.setVisibility(View.GONE)

                        listener.onCategoryClick(
                            courseData[position],3,null,mageview
                        )
                    },
                    1200
                )*/

                listener.onCategoryClick(
                    courseData[position],3,null,mageview
                )
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
            }

        })

        if (courseData[position].greekletter.length>0){
            greek_latter.visibility=View.VISIBLE
            greek_latter.text=courseData[position].greekletter
        }
        else{
            greek_latter.visibility=View.GONE
        }

        (v.findViewById<View>(R.id.tv_username) as TextView).setText(courseData[position].first_name)
        (v.findViewById<View>(R.id.age_text) as TextView).setText(courseData[position].age)

        (v.findViewById<View>(R.id.tv_university) as TextView).setText(courseData[position].university)

        (v.findViewById<View>(R.id.bright_img) as ImageView).setOnClickListener {
            listener.onCategoryClick(
                courseData[position], 1, null,mageview//done
            )
        }

        (v.findViewById<View>(R.id.star_view) as ImageView).setOnClickListener {
           /* (v.findViewById<View>(R.id.star_view) as ImageView).animate().scaleX(0.7f).setDuration(100).withEndAction {
                fab.animate().scaleX(1f).scaleY(1f)
            }*/
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

        (v.findViewById<View>(R.id.idIVCourse) as ImageView).setOnClickListener {}
        return v
    }
}