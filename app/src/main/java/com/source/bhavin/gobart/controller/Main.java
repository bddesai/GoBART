package com.source.bhavin.gobart.controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.source.bhavin.gobart.R;
import com.source.bhavin.gobart.model.Station;
import com.source.bhavin.gobart.view.FragmentFare;
import com.source.bhavin.gobart.view.FragmentRoute;
import com.source.bhavin.gobart.view.FragmentStationInfo;
import com.source.bhavin.gobart.view.MapsFragment;

import java.util.ArrayList;
import java.util.Arrays;


public class Main extends AppCompatActivity implements AdapterView.OnItemClickListener, FragmentStationInfo.OnFragmentInteractionListener{

    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] drawerItems;
    private ActionBarDrawerToggle drawerListener;
    Fragment mFragment;
    int[] drawerImages = {R.mipmap.ic_dollar, R.mipmap.ic_info, R.mipmap.ic_routemap};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mFragment = new FragmentFare();
            FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mainContent, mFragment, "fareFrag").commit();
        }else {
            mFragment = getSupportFragmentManager().findFragmentByTag("fareFrag");
        }
        drawerItems= getResources().getStringArray(R.array.drawerItems);
        // convert String array to arraylist
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(drawerItems));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the DrawerList
        listView = (ListView) findViewById(R.id.drawerList);
        listView.setAdapter(new DrawerListBaseAdapter(this, stringList, drawerImages));
        listView.setOnItemClickListener(this);

        //drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.mipmap.ic_drawer, R.string.drawer_open, R.string.drawer_close){
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        // set the DrawerToggle to the Drawer Layout
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerListener.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, drawerItems[position]+" was selected", Toast.LENGTH_SHORT).show();
        selectItem(position);
    }



    // selects the item handled in ListView and displays respective Fragments
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void selectItem(int position) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Fragment frag = null;
        Intent intent;

        switch (position) {
            case 0:
                frag = new FragmentFare();
                break;

            case 1:
                frag = new FragmentStationInfo();
                break;

            case 2:
                frag = new FragmentRoute();
                break;

        }
        ft.replace(R.id.mainContent, frag).addToBackStack("mainFrags").commit();

        listView.setItemChecked(position, true);
        setTitle("GoBART - "+drawerItems[position]);

        // Close drawer
        drawerLayout.closeDrawer(listView);
    }
    // Sets the Title in Action Bar to Value selected from Drawerlist
    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.action_exit:
                exitApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    public void exitApplication(){
        // Prepare the Dialog Box for exit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit GoBart?").setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Main.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // show the Exit Dialog Box
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onFragmentInteraction(Station station) {
        MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        String stationName = station.getName();
        double newlat = Double.parseDouble(station.getLatitude());
        double newlong = Double.parseDouble(station.getLongitude());
        if(mapsFragment!=null){
            mapsFragment.updateArticleView(newlat, newlong, stationName);
        }
        else{
            // Create fragment and give it an argument for the selected article
            MapsFragment newMapsFragment = new MapsFragment();
            newMapsFragment.populateMapsFragment(newlat, newlong, stationName);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.mainContent, newMapsFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}
