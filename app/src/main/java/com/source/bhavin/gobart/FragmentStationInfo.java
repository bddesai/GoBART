package com.source.bhavin.gobart;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStationInfo# newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStationInfo extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;

    private OnFragmentInteractionListener mListener;

    private ArrayList<Station> resultList = new ArrayList<>();


    private final String urlStns = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=";
    private final String key = "MW9S-E7SL-26DU-VV8V";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the spinner_item for this fragment
        View rootView = inflater.inflate(R.layout.fragment_station_info, container, false);

        // Identify your Listview from the spinner_item
        listView = (ListView) rootView.findViewById(R.id.stationListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Station station = resultList.get(position);
                mListener.onFragmentInteraction(station);
            }
        });



        //Start the getStations Task
        new getStations().execute();

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getActivity(), "Item Clicked",Toast.LENGTH_SHORT).show();
    }

    private class getStations extends AsyncTask<Void, Void, Void> {

        String xmlStr;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... params) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Make an HTTP call to our REST API and get the response string JSON data
            xmlStr = sh.makeServiceCall(urlStns+key, ServiceHandler.GET);

            if (xmlStr != null) {

                //Log.d("check", "Response inside doBackground: > " + xmlStr);

                // START PARSING XML DATA HERE
                XmlPullParserFactory pullParserFactory;
                XmlPullParser parser = null;

                try {
                    pullParserFactory = XmlPullParserFactory.newInstance();
                    parser  = pullParserFactory.newPullParser();

                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    InputStream stream = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
                    parser.setInput(stream, null);
                    resultList = parseXmlStations(parser);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Set the BaseAdapter to ListView for binding data
            listView.setAdapter(new StationListBaseAdapter(getActivity(), resultList));
        }
    }


    // Function to iterate and extract XML by filtering tags
    private ArrayList<Station> parseXmlStations(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Station> stations = null;
        int eventType = parser.getEventType();
        Station currentStation = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    stations = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if(name.equals("station")) {        // parent tag
                        currentStation = new Station();
                    }
                    else if(name.equals("name")) {      // name of the station
                        currentStation.setName(parser.nextText());
                    }
                    else if(name.equals("abbr")) {      // abbreviation of station
                        currentStation.setAbbr(parser.nextText());
                    }
                    else if(name.equals("gtfs_latitude")) {      // latitude of station
                        currentStation.setLatitude(parser.nextText());
                    }
                    else if(name.equals("gtfs_longitude")) {      // longitude of station
                        currentStation.setLongitude(parser.nextText());
                    }
                    else if(name.equals("address")) {      // city of station
                        currentStation.setAddress(parser.nextText());
                    }
                    else if(name.equals("city")) {      // city of station
                        currentStation.setCity(parser.nextText());
                    }
                    else if(name.equals("state")) {      // state of station
                        currentStation.setState(parser.nextText());
                    }
                    else if(name.equals("zipcode")) {      // zipcode of station
                        currentStation.setZipcode(parser.nextText());
                    }
                    break;

                case XmlPullParser.END_TAG:
                    name  = parser.getName();
                    if(name.equalsIgnoreCase("station") && currentStation != null) {
                        stations.add(currentStation);
                    }
                    break;
            }
            eventType = parser.next();
        }
        return stations;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Station station) {
        if (mListener != null) {
            mListener.onFragmentInteraction(station);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Station station);
    }


}
