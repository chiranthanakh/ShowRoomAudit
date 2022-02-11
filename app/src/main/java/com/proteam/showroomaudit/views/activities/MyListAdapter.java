package com.proteam.showroomaudit.views.activities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proteam.showroomaudit.R;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<Listviewmodel> {

    private final Activity context;
    private final ArrayList<Listviewmodel> subtitle;


    public MyListAdapter(Activity context, ArrayList<Listviewmodel> subtitle) {
        super(context,0, subtitle );
        // TODO Auto-generated constructor stub

        this.context=context;
        this.subtitle=subtitle;


    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);

        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        Listviewmodel listviewmodel = getItem(position);

        titleText.setText(String.valueOf(listviewmodel.getId()));

        subtitleText.setText(listviewmodel.getProductid());

        return rowView;

    };
}
