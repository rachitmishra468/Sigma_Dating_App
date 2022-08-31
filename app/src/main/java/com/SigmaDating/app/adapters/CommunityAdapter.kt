package com.SigmaDating.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.SigmaDating.R
import com.SigmaDating.app.model.communityModel.UniversityList


class CommunityAdapter(ctx: Context, countries: ArrayList<UniversityList>) : ArrayAdapter<UniversityList>(ctx, 0, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
        val country = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.customautotextview_layout,
            parent,
            false
        )
        val autoCompleteTextView = view!!.findViewById<View>(R.id.autoCompleteTextView) as TextView

        country?.let {
            autoCompleteTextView.text = country.name
        }
        return view
    }
}