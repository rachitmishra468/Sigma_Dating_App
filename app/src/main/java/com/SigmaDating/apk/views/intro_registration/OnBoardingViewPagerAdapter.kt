package com.SigmaDating.apk.views.intro_registration

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.SigmaDating.R


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

            3 -> BlankFragment4.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
            4 -> Password.newInstance(
                context.resources.getString(R.string.app_name),
                context.resources.getString(R.string.app_name)
            )
            5 -> Profile_Photo.newInstance(
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
        return 6
    }

}