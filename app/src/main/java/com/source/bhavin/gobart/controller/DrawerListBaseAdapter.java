package com.source.bhavin.gobart.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.source.bhavin.gobart.R;

import java.util.ArrayList;

/**
 * Created by Bhavin on 6/4/2015.
 */
public class DrawerListBaseAdapter extends BaseAdapter {


    private ArrayList<String> drawerList= new ArrayList<>();
    private int[] drawerImageList;
    private LayoutInflater inflater;
    private Context context;

    public DrawerListBaseAdapter(Context context, ArrayList<String> myList, int[] imageList) {
        this.drawerList = myList;
        this.context = context;
        this.drawerImageList = imageList;
    }

    @Override
    public int getCount() { return  drawerList.size(); }

    @Override
    public Object getItem(int position) { return drawerList.get(position); }

    @Override
    public long getItemId(int position) {  return  position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Set the spinner_item to single list item
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_drawer,parent,false);
        // Identify widgets from list_item.xml
        ImageView imageView = (ImageView) row.findViewById(R.id.image);
        TextView tvDrawerItem = (TextView) row.findViewById(R.id.drawerItemText);

        String itemName = drawerList.get(position);
        // Set the values to respective row
        imageView.setImageResource(drawerImageList[position]);
        tvDrawerItem.setText(itemName);

        return row;
    }
}