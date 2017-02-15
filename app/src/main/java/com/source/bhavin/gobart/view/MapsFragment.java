package com.source.bhavin.gobart.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.source.bhavin.gobart.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {

    private double mLatitude;
    private double mLongitude;
    private MapView mMapView;
    private String  stationName;


    public void populateMapsFragment(double latitude, double longitude, String statName) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.stationName = statName;
    }

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the spinner_item for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());
        GoogleMap googleMap = mMapView.getMap();
        LatLng targetPosition = new LatLng(mLatitude, mLongitude);

        MarkerOptions mMarker = new MarkerOptions();
        Marker marker = googleMap.addMarker(mMarker.position(targetPosition));

        //marker.setPosition(new LatLng(mLatitude, mLongitude));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue_black));
        marker.setTitle(stationName);
        marker.showInfoWindow();

        //googleMap.addMarker(mymarker);

        CameraPosition camera = new CameraPosition.Builder()
                .target(targetPosition).zoom(14).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(mLatitude, mLongitude, stationName);
        }
    }

    public void updateArticleView(double latitude, double longitude, String statName) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.stationName = statName;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
