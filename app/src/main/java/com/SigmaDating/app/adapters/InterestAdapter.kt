package com.SigmaDating.app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SigmaDating.R
import com.SigmaDating.app.model.communityModel.Interest
open class InterestAdapter (private var listener: InterestAdapter.OnItemClickListener, var stringtype: String)  : RecyclerView.Adapter<InterestAdapter.DataViewHolder>(),
    Filterable {


    //Image world pixels

    var photosList: ArrayList<Interest> = ArrayList()
    var photosListFiltered: ArrayList<Interest> = ArrayList()


    var onItemClick: ((Interest) -> Unit)? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(result: Interest) {
            val dd=  itemView.findViewById<TextView>(R.id.textView_school)
            dd.text=result.interest
            itemView.setOnClickListener {
                listener.onIntrestClick(result,stringtype);
            }

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


    fun addData(list: List<Interest>) {
        photosList = list as ArrayList<Interest>
        photosListFiltered = photosList
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onIntrestClick(position: Interest, stringtype: String)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Interest>()

                if (constraint == null || constraint.length == 0) {
                    filteredList.addAll(photosList);
                } else {
                    var filterPattern = constraint.toString().lowercase().trim();
                    photosList.filter { (it.interest.toLowerCase().startsWith(filterPattern))
                    }.forEach { filteredList.add(it) }
                    photosListFiltered = filteredList


                }

                val results = FilterResults()
                results.values = filteredList;

                return results;
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                photosListFiltered = if (results?.values == null) ArrayList()
                else
                    results.values as  ArrayList<Interest>
                notifyDataSetChanged()

                Log.e("performFiltering: t2 ", "called" + photosListFiltered.size)

            }
        }
    }
}