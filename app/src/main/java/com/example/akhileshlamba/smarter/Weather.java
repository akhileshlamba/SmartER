package com.example.akhileshlamba.smarter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by akhileshlamba on 18/3/18.
 */

public class Weather {

    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";


    private static final String OPEN_WEATHER_MAP_IMAGE
            = "https://openweathermap.org/img/w/";

    public static JSONObject getWeatherJSON(Double latitude, Double longitude){
        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, String.valueOf(latitude), String.valueOf(longitude)));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", "abef55c8fdd28a71db6ca59c322195b9");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                Log.e("error", "not found");
                return null;
            }

            connection.disconnect();

            return data;
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }


    public static Bitmap getImageData(String iconId){

        try {
            URL url = new URL(OPEN_WEATHER_MAP_IMAGE+iconId+".png");
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            //connection.addRequestProperty("x-api-key", "abef55c8fdd28a71db6ca59c322195b9");

            InputStream inputStream = connection.getInputStream();

            System.out.println("In method");

            if(inputStream != null){
                Bitmap map = BitmapFactory.decodeStream(inputStream);
                return map;
            }
            connection.disconnect();
            return null;
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }


}
