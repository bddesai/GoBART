package com.source.bhavin.gobart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bhavin on 4/30/2015.
 */
public class StationListBaseAdapter extends BaseAdapter {

    ArrayList<Station> stationList= new ArrayList<>();
    LayoutInflater inflater;
    Context context;

    public StationListBaseAdapter(Context context, ArrayList<Station> myList) {
        this.stationList = myList;
        this.context = context;
    }

    @Override
    public int getCount() { return  stationList.size(); }

    @Override
    public Object getItem(int position) { return stationList.get(position); }

    @Override
    public long getItemId(int position) {  return  position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Set the spinner_item to single list item
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item,parent,false);
        // Identify widgets from list_item.xml
        TextView tvStationName = (TextView) row.findViewById(R.id.stationName);
        TextView tvAddress = (TextView) row.findViewById(R.id.address);

        Station station = stationList.get(position);
        String address = station.getAddress()+", "+station.getCity()+", "+station.getState()+" - "+ station.getZipcode();
        // Set the values to respective row
        tvStationName.setText(station.getName());
        tvAddress.setText(address);

        return row;
    }
}
