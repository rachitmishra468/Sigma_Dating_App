package com.SigmaDating.apk.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.apk.model.EditProfiledata
import com.SigmaDating.apk.model.communityModel.UniversityList
import com.bumptech.glide.Glide

open class SchoolAdapter(private var listener: SchoolAdapter.OnItemClickListener, var stringtype: String)  : RecyclerView.Adapter<SchoolAdapter.DataViewHolder>(), Filterable {


    //Image world pixels

    var photosList: ArrayList<UniversityList> = ArrayList()
    var photosListFiltered: ArrayList<UniversityList> = ArrayList()


    var onItemClick: ((UniversityList) -> Unit)? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* init {
            itemView.setOnClickListener {
                onItemClick?.invoke(photosListFiltered[adapterPosition])
            }
        }*/

        fun bind(result: UniversityList) {
           // itemView.name.text = result.name + " " + result.author
           val dd=  itemView.findViewById<TextView>(R.id.textView_school)
            dd.text=result.name
            itemView.setOnClickListener {
                listener!!.onItClick(result,stringtype);
            }
           // Glide.with(itemView.imageView.context).load(result.downloadUrl).into(itemView.imageView)


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.school_list_item, parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(photosListFiltered[position])

    }

    override fun getItemCount(): Int = photosListFiltered.size


    fun addData(list: List<UniversityList>) {
        photosList = list as ArrayList<UniversityList>
        photosListFiltered = photosList
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItClick(position: UniversityList,stringtype: String)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {


                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) photosListFiltered =  ArrayList() else {
                    val filteredList = ArrayList<UniversityList>()
                 /*   for ( i in photosList ){
                        if (i.name.toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                            filteredList.add(i);
                        }
                    }
                    photosListFiltered=filteredList
*/

                    photosList
                        .filter {
                            (it.name.contains(constraint!!.substring(0, 1).toUpperCase() + constraint.substring(1)))

                        }
                        .forEach { filteredList.add(it) }
                    photosListFiltered = filteredList

                    Log.e("performFiltering: t1: ", filteredList.size.toString())

                }
                return FilterResults().apply { values = photosListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                photosListFiltered = if (results?.values == null) ArrayList()
                else
                    results.values as  ArrayList<UniversityList>
                    notifyDataSetChanged()

                Log.e("performFiltering: t2 ", "called" + photosListFiltered.size)

            }
        }
    }
}