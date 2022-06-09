package com.example.sigmadatingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sigmadatingapp.R;
import com.example.sigmadatingapp.module.Profile;

import java.util.ArrayList;

public class ProfileMatch extends BaseAdapter {

    // on below line we have created variables
    // for our array list and context.
    private ArrayList<Profile> courseData;
    private Context context;

    // on below line we have created constructor for our variables.
    public ProfileMatch(ArrayList<Profile> courseData, Context context) {
        this.courseData = courseData;
        this.context = context;
    }

    @Override
    public int getCount() {
        // in get count method we are returning the size of our array list.
        return courseData.size();
    }

    @Override
    public Object getItem(int position) {
        // in get item method we are returning the item from our array list.
        return courseData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // in get item id we are returning the position.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // in get view method we are inflating our layout on below line.
        View v = convertView;
        if (v == null) {
            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_match_layout, parent, false);
        }
        // on below line we are initializing our variables and setting data to our variables.
       // ((TextView) v.findViewById(R.id.idTVCourseName)).setText(courseData.get(position).getName());
        ((ImageView) v.findViewById(R.id.idIVCourse)).setImageResource(courseData.get(position).getProfile_img());
        return v;
    }
}
