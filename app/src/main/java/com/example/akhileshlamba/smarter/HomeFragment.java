package com.example.akhileshlamba.smarter;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.akhileshlamba.smarter.entities.ElectricityUsage;
import com.example.akhileshlamba.smarter.entities.House;
import com.example.akhileshlamba.smarter.entities.ResCredientials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class HomeFragment extends android.app.Fragment implements View.OnClickListener{

    public HomeFragment() {
        handler = new Handler();
    }

    private Handler handler;
    private TextView tempid;
    private ImageView imageid;
    private TextClock textClock;
    private Button usage;
    private Button server;
    private ImageView usageImage;
    private TextView usageText;

    int count = 0;
    private ElectricityUsageSimulator simulator;
    private ElectricityDatabase database;
    private double temperature = 0;

    private static int START_PEAK_HOUR = 9;
    private static int END_PEAK_HOUR = 22;

    private int insertOne = 0;


    Bundle bundle;
    House house;
    ResCredientials resCredientials;
    double lat = 0.0;
    double lon = 0.0;

    View fragmentHome;

    SimpleDateFormat format;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHome =  inflater.inflate(R.layout.fragment_home, container, false);

        format = new SimpleDateFormat("yyyy-MM-dd");

        simulator = new ElectricityUsageSimulator();
        database = new ElectricityDatabase(getActivity().getApplicationContext());

        Bundle bundle = getArguments();

        /*Intent intent = getIntent();*/
        house = (House) bundle.getParcelable("house");
        resCredientials = (ResCredientials) bundle.getParcelable("credientials");

        //Log.i("House", String.valueOf(house.getResid()));

        tempid = (TextView) fragmentHome.findViewById(R.id.tempID);
        imageid = (ImageView) fragmentHome.findViewById(R.id.imageTempID);
        textClock = (TextClock) fragmentHome.findViewById(R.id.clockID);
        usage = (Button) fragmentHome.findViewById(R.id.usageButton);
        server = (Button) fragmentHome.findViewById(R.id.serverButton);
        usageImage = (ImageView) fragmentHome.findViewById(R.id.usageImage);
        usageText = (TextView) fragmentHome.findViewById(R.id.usageText);

        usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToServerDatabase(v);
            }
        });

        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            List<Address> address = geocoder.getFromLocationName(house.getAddress() + " " + house.getPostcode(), 1);
            lat = address.get(0).getLatitude();
            lon = address.get(0).getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("latlon", String.valueOf(lat) + " " + String.valueOf(lon));
        textClock.setFormat12Hour(TextClock.DEFAULT_FORMAT_12_HOUR);

        updateWeather();
        //new WeatherTask().execute();


        return fragmentHome;
    }


    @Override
    public void onClick(View v) {
        addData();
    }

    public double getUsage(int hour){
        try{
            database.open();
        }catch(SQLException e){
            Log.e("SQLException", e.toString());
        }
        double sum = 0.0;
        Cursor c = database.getHourUsages(hour);
        Log.i("Database: ", c.toString());
        ArrayList<ElectricityUsage> usages = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                sum += c.getDouble(2) + c.getDouble(3) + c.getDouble(4);
                Log.i("Database value: ", c.getString(2) + " " + c.getString(3) + " " + c.getString(4));
            }while(c.moveToNext());
        }
        return sum;
    }

    public void addData(){
        ElectricityUsage electricityUsage = new ElectricityUsage();
        electricityUsage.setResid(house.getResid());
        if(temperature < 20.0)
            electricityUsage.setAirconditionerUsage(0.0);
        else
            electricityUsage.setAirconditionerUsage(simulator.setACUsage());
        electricityUsage.setFridgeUsage(simulator.setFRUsage());
        electricityUsage.setWashingmachineUsage(simulator.setWMUsage());
        electricityUsage.setCurrentdate(new java.sql.Date(System.currentTimeMillis()));
        electricityUsage.setDayHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        electricityUsage.setTemperature(temperature);

        try{
            database.open();
        }catch(SQLException e){
            Log.e("SQLException", e.toString());
        }

        database.insertUser(electricityUsage);
        database.close();
    }

    public void writeToServerDatabase(View view){
        try{
            database.open();
        }catch(SQLException e){
            Log.e("SQLException", e.toString());
        }
        Cursor c = database.getAllUsages(house.getResid());
        Log.i("Database: ", c.toString());
        ArrayList<ElectricityUsage> usages = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                ElectricityUsage electricityUsage = new ElectricityUsage();
                electricityUsage.setResid(c.getInt(1));
                electricityUsage.setAirconditionerUsage(c.getDouble(2));
                electricityUsage.setWashingmachineUsage(c.getDouble(3));
                electricityUsage.setFridgeUsage(c.getDouble(4));
                electricityUsage.setTemperature(c.getDouble(5));
                try {
                    electricityUsage.setCurrentdate(format.parse(c.getString(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                electricityUsage.setDayHour(c.getInt(7));
                usages.add(electricityUsage);
                Log.i("Database value: ", c.getString(4));
            }while(c.moveToNext());
        }
        new WriteToServerTask().execute(usages);
        database.deleteAllRecords(house.getResid());
        database.close();


    }

    public void updateWeather(){

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    while(!isInterrupted()){
                        final JSONObject json = Weather.getWeatherJSON( lat, lon);
                        Log.i("json", json.toString());
                        final Bitmap image = Weather.getImageData(json.getJSONArray("weather")
                                .getJSONObject(0).get("icon").toString());

                        if (json != null) {
                            handler.post(new Runnable() {
                                public void run() {
                                    renderWeather(json, image);
                                }
                            });
                        }
                        Thread.sleep(1000*60*60);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void renderWeather(JSONObject object, Bitmap image){
        try{
            temperature = object.getJSONObject("main").getDouble("temp");
            tempid.setText(object.getJSONObject("main").get("temp").toString());
            imageid.setImageBitmap(image);
            if(insertOne < 1){

                addData();
                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                Log.i("Boolean", String.valueOf(peakHour()));
                if(getUsage(currentHour) < 1.5 && peakHour()){
                    usageImage.setImageResource(R.drawable.green);
                    usageText.setText("You are saving electricity. Keep up the good work");
                } else{
                    usageImage.setImageResource(R.drawable.red);
                    usageText.setText("Save electricity for future");
                }
                insertOne++;
            }
        }catch(Exception e){
            Log.e("ExceptionHome: ", e.toString());
        }
    }


    private boolean peakHour(){
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(currentHour < END_PEAK_HOUR && currentHour >= START_PEAK_HOUR &&(dayOfWeek != 1 || dayOfWeek != 6 || dayOfWeek !=7))
            return true;
        else
            return false;
    }

    private class AddressTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "com.project.entities.house/findAllHouses";
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
                bundle.putIntegerArrayList("resids", ids);
                bundle.putStringArrayList("address", addresses);

                /*Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("addresses", bundle);
                startActivity(intent);*/

            }else{

            }



        }
    }

    private class WriteToServerTask extends AsyncTask<ArrayList<ElectricityUsage>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<ElectricityUsage>... usages) {
            String url1 = "com.project.entities.electricityusage/createUsageEntry";
            HttpURLConnection urlConnection = RESTConnection.createConnection(url1);
            try{

                ArrayList<ElectricityUsage> electricityUsages = usages[0];

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                JSONArray array = new JSONArray();
                for(ElectricityUsage electricityUsage: electricityUsages){
                    JSONObject object = new JSONObject();
                    object.put("usageid",electricityUsage.getUsageid());
                    object.put("resid",electricityUsage.getResid());
                    object.put("ac",electricityUsage.getAirconditionerUsage());
                    object.put("wm",electricityUsage.getWashingmachineUsage());
                    object.put("fridge",electricityUsage.getFridgeUsage());
                    object.put("temperature",electricityUsage.getTemperature());
                    object.put("date",format.format(electricityUsage.getCurrentdate()));
                    object.put("hour",electricityUsage.getDayHour());
                    array.put(object);
                }
                JSONObject houseObject = new JSONObject();
                houseObject.put("firstname", house.getFirstName());
                houseObject.put("lastname", house.getLastName());
                houseObject.put("resid", house.getResid());
                houseObject.put("emailId", house.getEmail());
                houseObject.put("phone", house.getMobile());
                houseObject.put("code", house.getPostcode());
                houseObject.put("dateOfBirth", house.getDob());
                houseObject.put("energyProvider", house.getEnergyproviderName());
                houseObject.put("add", house.getAddress());
                houseObject.put("occupants", house.getNoOfOccupants());
                array.put(houseObject);

                OutputStreamWriter wr= new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");

                wr.write(array.toString());
                wr.flush();
                wr.close();

                Log.i("URLCOnnection", urlConnection.toString());

                int code = urlConnection.getResponseCode();
                Log.i("code", String.valueOf(code));
                return null;

            }catch(Exception e){
                Log.e("Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
        }
    }

}
