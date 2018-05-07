package com.example.akhileshlamba.smarter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;



public class MapsFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener{

    public MapsFragment() {
        // Required empty public constructor
    }


    View fragmentMap;
    private GoogleMap mMap;
    ArrayList<String> address;
    ArrayList<Integer> resids;
    int resid = 0;
    Spinner spinner;
    String value = "";
    MarkerOptions options = new MarkerOptions();
    LatLng latlon ;
    double lat;
    double lon;
    Geocoder geocoder;
    private static final double MAX_HOUR_USAGE = 1.5;
    private static final double MAX_DAILY_USAGE = 21.0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentMap = inflater.inflate(R.layout.fragment_maps, container, false);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getArguments();
        address = bundle.getStringArrayList("address");
        resids = bundle.getIntegerArrayList("resid");

        Log.i("Size", address.size() + " " + resids.size());

        spinner = (Spinner) fragmentMap.findViewById(R.id.mapSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.usageType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        geocoder = new Geocoder(getActivity().getApplicationContext());

        return fragmentMap;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        /*geocoder = new Geocoder(getActivity().getApplicationContext());
        try {

            for(int i = 0; i < address.size(); i++){
                List<Address> addresses = geocoder.getFromLocationName(address.get(i), 1);
                Log.i("address", address.get(i));
                double lat = addresses.get(0).getLatitude();
                double lon = addresses.get(0).getLongitude();
                LatLng latlon = new LatLng(lat, lon);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setBuildingsEnabled(true);


                *//*mMap.addMarker(options.position(latlon).title("Marker in "+address.get(i)));*//*
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        } catch (IOException e) {
            Log.e("Error in Maps", e.toString());
        }*/

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        value = (String) parent.getItemAtPosition(position);

        String[] list = {String.valueOf(resid), value};
        Log.i("Item", String.valueOf(resid));
        new UsageTask().execute(list);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class UsageTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            int resid = Integer.valueOf(voids[0]);
            String view  = voids[1];
            Date date = new Date(System.currentTimeMillis());
            Log.i("Resident id", String.valueOf(resid));
            int hour = 14;
            String url1 = "";

            if(view.equalsIgnoreCase("hourly")){
                url1 = "com.project.entities.electricityusage/findHourlyUsageofAllAppliancesForAllResident/" + "2018-03-22" + "/" + hour;
            } else if(view.equalsIgnoreCase("daily")){
                url1 = "com.project.entities.electricityusage/findTotalDailyUsageofAllAppliancesOfAllResidents/" + "2018-03-22";
            }

            HttpURLConnection urlConnection = RESTConnection.createConnection(url1);
            try{

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONArray data = new JSONArray(json.toString());
                Log.i("JSON", json.toString());
                return data;

            }catch(Exception e){
                Log.e("Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray != null) {
                Log.i("JsonResult", jsonArray.toString());
                try {
                    if(jsonArray.getJSONObject(0).getString("view").equalsIgnoreCase("hourly")){
                        mMap.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String mapAddress = jsonArray.getJSONObject(i).getString("address") + " " + jsonArray.getJSONObject(i).getInt("postcode");
                            Double usage = jsonArray.getJSONObject(i).getDouble("totalUsage");
                            if (address.contains(mapAddress)) {
                                if(usage < MAX_HOUR_USAGE)
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                else
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                List<Address>addresses = geocoder.getFromLocationName(mapAddress, 1);
                                double lat = addresses.get(0).getLatitude();
                                double lon = addresses.get(0).getLongitude();
                                LatLng latlon = new LatLng(lat, lon);
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                mMap.setBuildingsEnabled(true);


                                /*mMap.addMarker(options.position(latlon).title("Marker in "+address.get(i)));*/
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                mMap.addMarker(options.position(latlon).title("Usage is "+ usage));
                            }
                        }
                    } else {
                        mMap.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String mapAddress = jsonArray.getJSONObject(i).getString("address") + " " + jsonArray.getJSONObject(i).getInt("postcode");
                            Double usage = jsonArray.getJSONObject(i).getDouble("total usage");
                            if (address.contains(mapAddress)) {
                                if(usage < MAX_DAILY_USAGE)
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                else
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                List<Address>addresses = geocoder.getFromLocationName(mapAddress, 1);
                                double lat = addresses.get(0).getLatitude();
                                double lon = addresses.get(0).getLongitude();
                                LatLng latlon = new LatLng(lat, lon);
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                mMap.setBuildingsEnabled(true);
                                /*mMap.addMarker(options.position(latlon).title("Marker in "+address.get(i)));*/
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                mMap.addMarker(options.position(latlon).title("Usage is "+ usage));
                            }
                        }
                    }
                } catch(Exception e){
                    Log.e("Exception", e.toString());
                }
            }
        }
    }


}
