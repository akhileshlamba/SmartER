package com.example.akhileshlamba.smarter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by akhileshlamba on 28/3/18.
 */

public class RESTConnection {


    private static final String BASE_URL = "http://10.0.2.2:8080/5046Project/webresources/";

    public static HttpURLConnection createConnection(String url1){
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(BASE_URL + url1);
            urlConnection = (HttpURLConnection) url.openConnection();
        }
        catch(Exception e){

        }
        return urlConnection;
    }

    public static JSONArray validateUser(HttpURLConnection urlConnection){
        try{
            //urlConnection.setRequestMethod("POST");
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
        }catch (Exception e){
            Log.e("Error: ", e.toString());
        }
        return null;
    }


    public static JSONObject createUser(HttpURLConnection urlConnection){
        try {
            urlConnection.setRequestMethod("POST");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
