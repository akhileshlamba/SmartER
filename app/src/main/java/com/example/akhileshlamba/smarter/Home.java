package com.example.akhileshlamba.smarter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.akhileshlamba.smarter.entities.House;
import com.example.akhileshlamba.smarter.entities.ResCredientials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    Bundle bundle;
    House house;
    ResCredientials resCredientials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new HomeFragment();

        Intent intent = getIntent();
        bundle = (Bundle) intent.getParcelableExtra("profile");
        house = (House) bundle.getParcelable("house");
        resCredientials = (ResCredientials) bundle.getParcelable("credientials");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nView =  navigationView.getHeaderView(0);

        TextView nameView = (TextView) nView.findViewById(R.id.nameView);
        nameView.setText(house.getFirstName()+" " + house.getLastName());

        TextView emailView = (TextView) nView.findViewById(R.id.emailView);
        emailView.setText(house.getEmail());

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;
        if(id == R.id.homepage){
            nextFragment = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            nextFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame,
                    nextFragment).commit();
        }

        if (id == R.id.map) {
            new AddressTask().execute();
        } else if (id == R.id.pie) {
            /*Intent intent = new Intent(this, PieChartActivity.class);
            intent.putExtra("resid", house.getResid());*/
            Bundle bundle = new Bundle();
            bundle.putInt("resid", house.getResid());
            nextFragment = new PieChartFragment();
            nextFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,
                    nextFragment).commit();
            //startActivity(intent);
        } else if (id == R.id.bar) {
            Bundle bundle = new Bundle();
            bundle.putInt("resid", house.getResid());
            nextFragment = new BarGraphFragment();
            nextFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,
                    nextFragment).commit();
        } else if (id == R.id.line) {
            Bundle bundle = new Bundle();
            bundle.putInt("resid", house.getResid());
            nextFragment = new LineGraphFragment();
            nextFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,
                    nextFragment).commit();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class AddressTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "com.project.entities.house/findAllHouses";
            JSONArray data = null;
            HttpURLConnection urlConnection = RESTConnection.createConnection(url1);
            try{
                Log.i("URLCOnnection", urlConnection.toString());
                int code = urlConnection.getResponseCode();
                Log.i("code", String.valueOf(code));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                data = new JSONArray(json.toString());
                Log.i("JSON", json.toString());


            }catch(Exception e){
                Log.e("Exception", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray != null || jsonArray.length() != 0){
                Log.i("Json Result", jsonArray.toString());
                ArrayList<String> addresses = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                for(int i = 0; i< jsonArray.length(); i++){
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        addresses.add(object.getString("address") + " " + object.getInt("postcode"));
                        ids.add(object.getInt("resid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("address", addresses);
                bundle.putIntegerArrayList("resid", ids);
                Fragment nextFragment = null;
                nextFragment = new MapsFragment();
                nextFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        nextFragment).commit();

            }else{

            }



        }
    }
}
