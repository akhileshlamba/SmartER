package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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


public class PieChartFragment extends Fragment {


    public PieChartFragment() {
        // Required empty public constructor
    }

    private static String TAG = "PieChart";
    int resid;
    ArrayList<Float> data = new ArrayList<>();
    ArrayList<String> appliances = new ArrayList<>();

    com.github.mikephil.charting.charts.PieChart pieChart;

    private JSONArray json;
    View fragmentPieChart;
    private static TextView datePie;
    private Button pickDate;
    private Button setButton;
    private static String date = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentPieChart =  inflater.inflate(R.layout.fragment_pie_chart, container, false);

        Bundle bundle = getArguments();
        resid = bundle.getInt("resid");



        datePie = (TextView) fragmentPieChart.findViewById(R.id.pieDate);
        pickDate = (Button) fragmentPieChart.findViewById(R.id.pickDate);
        setButton = (Button) fragmentPieChart.findViewById(R.id.setButton);

        pieChart = (com.github.mikephil.charting.charts.PieChart) fragmentPieChart.findViewById(R.id.pieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Appliance Usage");
        pieChart.setCenterTextSize(10);

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] voids = {String.valueOf(resid), datePie.getText().toString()};
                if(datePie.getText().equals("Date"))
                    Toast.makeText(getActivity().getApplicationContext(), "Please select date to view Report", Toast.LENGTH_LONG).show();
                else
                    new PieChartReportTask().execute(voids);
            }
        });

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
                Toast.makeText(getActivity().getApplicationContext(), "Appliance: " + appliance + "\n" + "Usage: " + sales , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }


        });


        return fragmentPieChart;
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


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            date = populateSetDate(yy, mm + 1, dd);
            Log.i("date", date);
            datePie.setText(date);
        }

        public String populateSetDate(int year, int month, int day) {
            StringBuilder dateSet = new StringBuilder();
            dateSet.append(year);
            //String dateSet = "";
            if ((month) < 10)
                dateSet.append("-0" + (month));
            else
                dateSet.append("-" + (month));

            if (day < 10)
                dateSet.append("-0" + day);
            else
                dateSet.append("-" + day);

            return dateSet.toString();
        }
    }

}
