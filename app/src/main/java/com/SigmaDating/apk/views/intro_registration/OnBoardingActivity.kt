package com.SigmaDating.apk.views.intro_registration

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.SigmaDating.R
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import com.SigmaDating.apk.utilities.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var pageIndicator: TabLayout
    private var img_back: ImageView? = null
    val userRegister: User_Register by viewModels()


    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        mViewPager = findViewById(R.id.viewPager)
        img_back = findViewById<ImageView>(R.id.img_back)
        pageIndicator = findViewById(R.id.pageIndicator)
        mViewPager.adapter = OnBoardingViewPagerAdapter(this, this)
        mViewPager.setUserInputEnabled(false);
        mViewPager.offscreenPageLimit = 1
        mViewPager.setPageTransformer(ZoomOutPageTransformer(1))
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    // btnNext.text = getText(R.string.finish)
                } else {
                    // btnNext.text = getText(R.string.next)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()


        img_back?.setOnClickListener {
            if (getItem() == 0) {
                finish()
            } else {
                mViewPager.setCurrentItem(getItem() - 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        mViewPager.setCurrentItem(item, smoothScroll)
    }
}
