package com.thaivan.bay.branch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.item.listview_item;

import java.util.ArrayList;

public class listview_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<listview_item> data;
    private int layout;

    public listview_adapter(Context context, int layout, ArrayList<listview_item> data) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }


    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getName();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        listview_item listviewitem = data.get(position);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageview);
        icon.setImageResource(listviewitem.getIcon());
        TextView name = (TextView) convertView.findViewById(R.id.textview);
        name.setText(listviewitem.getName());
        return convertView;
    }

}
