package com.SigmaDating.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SigmaDating.R;
import com.SigmaDating.app.model.communityModel.UniversityList;

import java.util.ArrayList;
import java.util.List;

public class Filter_data_Adapter extends RecyclerView.Adapter<Filter_data_Adapter.ExampleViewHolder> implements Filterable {
    private List<UniversityList> exampleList;
    private List<UniversityList> exampleListFull;

    class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;


        ExampleViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView_school);

        }
    }

  public  Filter_data_Adapter(List<UniversityList> exampleList) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_list_item,
                parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        UniversityList currentItem = exampleList.get(position);


        holder.textView1.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UniversityList> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UniversityList item : exampleListFull) {
                    if (item.getName().toLowerCase().substring(0,constraint.length()).contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



}