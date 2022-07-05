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
    private val schoolList: MutableList<UniversityList> = ArrayList(cities)

    override fun getCount(): Int {
        return schoolList.size
    }
    override fun getItem(position: Int): UniversityList {
        return schoolList[position]
    }
    override fun getItemId(position: Int): Long {
        return schoolList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val schoolList: UniversityList = getItem(position)
            val schoolListAutoCompleteView = convertView!!.findViewById<View>(R.id.autoCompleteTextView) as TextView
            schoolListAutoCompleteView.text = schoolList.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

}