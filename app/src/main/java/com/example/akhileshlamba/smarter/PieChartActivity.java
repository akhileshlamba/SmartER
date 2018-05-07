package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;

public class PieChartActivity extends AppCompatActivity {

    private static String TAG = "PieChart";

    ArrayList<Float> data = new ArrayList<>();
    ArrayList<String> appliances = new ArrayList<>();

    com.github.mikephil.charting.charts.PieChart pieChart;

    private JSONArray json;
    private int resid;
    private TextView datePie;


    public void viewReport(View view){
        String[] voids = {String.valueOf(resid), datePie.getText().toString()};
        new PieChartReportTask().execute(voids);
    }

    public void pickDate(View view){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DATE);

        month = month;

        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
                StringBuilder dateSet = new StringBuilder();
                dateSet.append(i);
                //String dateSet = "";
                if((i1+1) < 10)
                    dateSet.append("-0"+(i1+1));
                else
                    dateSet.append("-"+(i1+1));

                if(i2<10)
                    dateSet.append("-0"+i2);
                else
                    dateSet.append("-"+i2);
                datePie.setText(dateSet);
            }
        }, year, month, dayOfMonth);
        pickerDialog.show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datePie = (TextView) findViewById(R.id.datePie);

        pieChart = (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.pieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Appliance Usage");
        pieChart.setCenterTextSize(10);

        resid = getIntent().getIntExtra("resid",0);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("y: ");
                Log.d(TAG,"pos is: " + pos1);
                String sales = e.toString().substring(pos1 + 2);
                Log.d(TAG,"Value is: " + sales);

                int index = 0;
                for (int i = 0; i< data.size(); i++) {
                    index = data.indexOf(Float.valueOf(sales));
                }
                String appliance = appliances.get(index);
                Toast.makeText(PieChartActivity.this, "Appliance: " + appliance + "\n" + "Usage: " + sales , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }


        });

    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();


        if(!data.isEmpty()){
            data = new ArrayList<>();
        }
        try {
            data.add((float)json.getJSONObject(0).getDouble("fridge_usage"));
            data.add((float)json.getJSONObject(0).getDouble("airconditioner_usage"));
            data.add((float)json.getJSONObject(0).getDouble("washingmachine_usage"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for( int i =0; i< data.size(); i++)
            yEntrys.add(new PieEntry(data.get(i) , i));

        appliances.add("Fridge");
        appliances.add("Airconditioner");
        appliances.add("Washing Machine");

        for (int i = 0; i<appliances.size(); i++)
            xEntrys.add(appliances.get(i));

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Electricity Usage");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }



    private class PieChartReportTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "com.project.entities.electricityusage/findTotalDailyUsageofEachAppliance/";
            HttpURLConnection urlConnection = RESTConnection.createConnection(url1 + voids[0] + "/" + voids[1]);
            try{
                Log.i("URLConnection", urlConnection.toString());
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
                json = jsonArray;
                addDataSet();

            }else{

            }



        }
    }

}
