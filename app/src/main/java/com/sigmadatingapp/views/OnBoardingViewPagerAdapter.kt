package com.sigmadatingapp.views

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sigmadatingapp.R


class OnBoardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BlankFragment.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
            1 -> BlankFragment2.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
            2 -> BlankFragment3.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
            else -> BlankFragment.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

}