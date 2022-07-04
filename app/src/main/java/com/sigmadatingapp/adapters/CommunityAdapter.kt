package com.sigmadatingapp.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sigmadatingapp.R
import com.sigmadatingapp.model.communityModel.UniversityList


class CommunityAdapter (private val mContext: Context,
                        private val mLayoutResourceId: Int,
                        cities: List<UniversityList>) :
    ArrayAdapter<UniversityList>(mContext, mLayoutResourceId, cities)
{
    private val city: MutableList<UniversityList> = ArrayList(cities)

    override fun getCount(): Int {
        return city.size
    }
    override fun getItem(position: Int): UniversityList {
        return city[position]
    }
    override fun getItemId(position: Int): Long {
        return city[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val city: UniversityList = getItem(position)
            val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.autoCompleteTextView) as TextView
            cityAutoCompleteView.text = city.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

}