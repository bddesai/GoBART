package com.source.bhavin.gobart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FragmentFare extends Fragment {

    Spinner sp1;
    Spinner sp2;
    Button bt;
    TextView tv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fare, container, false);

        final String apiUrl = "http://api.bart.gov/api/stn.aspx?cmd=stns";
        final String apiUrl2 = "http://api.bart.gov/api/sched.aspx?cmd=fare&orig=";
        final String apiKey = "QWVJ-USED-IPZQ-DT35";


        sp1 = (Spinner) rootView.findViewById(R.id.spinner);
        sp2 = (Spinner) rootView.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, new ArrayList<String>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);

        String url = apiUrl + "&key=" + apiKey;
        new TaskPopulateStation().execute(url);

        bt = (Button) rootView.findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orig = sp1.getSelectedItem().toString();
                String dest = sp2.getSelectedItem().toString();
                orig = orig.substring(orig.length() - 5, orig.length() - 1);
                dest = dest.substring(dest.length() - 5, dest.length() - 1);

                // if source and destination are same, throw a message
                if(orig.equals(dest)) {
                    Toast.makeText(getActivity(), "Source and Destination are same.", Toast.LENGTH_SHORT).show();
                    tv.setText(" ");
                }else{
                    String url2 = apiUrl2 + orig + "&dest=" + dest + "&date=today&key=" + apiKey;
                    new TaskCalculateFare().execute(url2);
                }
            }
        });

        tv = (TextView) rootView.findViewById(R.id.textView2);

        // Inflate the spinner_item for this fragment
        return rootView;
    }


    // Parsing the XML to accumulate station information
    private ArrayList<Station> parseXmlStations(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Station> stations = null;
        int eventType = parser.getEventType();
        Station currentStation = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    stations = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "station":         // parent tag
                            currentStation = new Station();
                            break;
                        case "name":       // name of the station
                            assert currentStation != null;
                            currentStation.setName(parser.nextText());
                            break;
                        case "abbr":       // abbreviation of station
                            assert currentStation != null;
                            currentStation.setAbbr(parser.nextText());
                            break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name  = parser.getName();
                    if(name.equalsIgnoreCase("station") && currentStation != null) {
                        assert stations != null;
                        stations.add(currentStation);
                    }
                    break;
            }
            eventType = parser.next();
        }
        return stations;
    }

    //  Parsing the API querystring to calculate fare
    private String parseXmlFare(XmlPullParser parser) throws XmlPullParserException, IOException {
        String fare = null;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if(name.equals("fare")) {
                        fare = parser.nextText();
                    }
                    break;
            }
            eventType = parser.next();
        }
        return fare;
    }

    /* Method to bind stations names to ArrayAdapters of  Spinner
        and it is a helper function of AsyncTask
    */
    private void addStations(ArrayList<Station> stations) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) sp1.getAdapter();
        for(Station st: stations){
            adapter.add(st.getName() + " (" + st.getAbbr() + ")");
        }
    }

    // method to display fare value
    private void showFare(String fare) {
        tv.setVisibility(View.VISIBLE);
        tv.setText(fare + "$");
    }

    // Background task to pull and bind station names to Spinners
    private class TaskPopulateStation extends AsyncTask<String, Void, ArrayList<Station>> {

        @Override
        protected ArrayList<Station> doInBackground(String... params) {
            String urlString = params[0];
            ArrayList<Station> result = new ArrayList<>();
            InputStream in = null;
            URL url;

            try {
                url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            XmlPullParserFactory pullParserFactory;
            XmlPullParser parser;

            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                parser  = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                result = parseXmlStations(parser);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Station> arrayList) {
            // Set the ArrayAdapter to bind the Spinners
            addStations(arrayList);
        }
    }

    //  Background task to show fare
    private class TaskCalculateFare extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String result = null;
            InputStream in = null;
            URL url;

            try {
                url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            XmlPullParserFactory pullParserFactory;
            XmlPullParser parser;

            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                parser  = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                result = parseXmlFare(parser);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String fare) {
            showFare(fare);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
