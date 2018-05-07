package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LineGraphFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    View fragmentLineGraph;
    private static String TAG = "BarChart";
    Spinner spinner;
    JSONArray json;
    ArrayList<Float> usageData = new ArrayList<>();
    ArrayList<Float> temperatureData = new ArrayList<>();
    private String value;
    LineChart lineChart;
    private int resid= 0;
    private static String date = "";
    private static TextView datePie;
    private Button pickDate;
    private Button setButton;


    public LineGraphFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentLineGraph =  inflater.inflate(R.layout.fragment_line_graph, container, false);

        resid = getArguments().getInt("resid");
        datePie = (TextView) fragmentLineGraph.findViewById(R.id.tLDate);
        pickDate = (Button) fragmentLineGraph.findViewById(R.id.pLDate);
        setButton = (Button) fragmentLineGraph.findViewById(R.id.vLReport);


        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new LineGraphDatePickerFragment();

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        spinner = (Spinner) fragmentLineGraph.findViewById(R.id.barPicker);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.usageType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        lineChart =(LineChart) fragmentLineGraph.findViewById(R.id.lineChart);


        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] voids = {spinner.getSelectedItem().toString(), String.valueOf(resid), datePie.getText().toString()};
                if(datePie.getText().equals("Date") && spinner.getSelectedItem().toString().equals("Hourly"))
                    Toast.makeText(getActivity().getApplicationContext(), "Please select date to view Report", Toast.LENGTH_LONG).show();
                else
                    new LineChartReportTask().execute(voids);


            }
        });

        return fragmentLineGraph;
    }

    private void addDataSet(String view) {
        Log.d(TAG, "addDataSet started");
        ArrayList<Entry> usageEntrys = new ArrayList<>();
        ArrayList<Entry> temperatureEntrys = new ArrayList<>();
        final ArrayList<String> xEntrys = new ArrayList<>();

        if(view.equalsIgnoreCase("daily")){
            for (int i = 1; i<32; i++){
                if(i<10)
                    xEntrys.add("2018-03" + "-0"+i);
                else
                    xEntrys.add("2018-03" + "-"+i);
            }
        } else {
            for (int i = 0; i<24; i++)
                xEntrys.add(String.valueOf(i));
        }


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.i("Value: ", xEntrys.get((int)value));
                return xEntrys.get((int)value);
            }
        };


        try {
            if(!usageData.isEmpty()){
                usageData = new ArrayList<>();
                temperatureData = new ArrayList<>();
            }
            for (int i = 0; i<json.length(); i++){
                JSONObject object = json.getJSONObject(i);
                usageData.add((float) object.getDouble("usage"));
                temperatureData.add((float) object.getDouble("temperature"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for( int i =0; i< usageData.size(); i++)
            usageEntrys.add(new Entry(i, usageData.get(i)));

        for( int i =0; i< temperatureData.size(); i++)
            temperatureEntrys.add(new Entry(i, temperatureData.get(i)));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //create the data set
        dataSets.add(new LineDataSet(usageEntrys, "Electricity Usage"));
        dataSets.add(new LineDataSet(temperatureEntrys, "Temperature"));

        LineData lineData = new LineData(dataSets);

        //lineData.setBarWidth(0.4f);

        lineChart.setData(lineData);
        Description description = new Description();
        description.setText("Usage");
        //barChart.setDescription(description);
        XAxis xAxisFromChart = lineChart.getXAxis();
        xAxisFromChart.setValueFormatter(formatter);
        lineChart.invalidate();
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("y: ");
                Log.d(TAG,"pos is: " + pos1);
                String usage = e.toString().substring(pos1 + 2);
                Log.d(TAG,"Value is: " + usage);

                int index = 0;
                for (int i = 0; i< usageData.size(); i++) {
                    index = usageData.indexOf(Float.valueOf(usage));
                }
                String hour = xEntrys.get(index);
                Toast.makeText(getActivity().getApplicationContext(), "Hour: " + hour + "\n" + "Usage: " + usage , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        value = (String) parent.getItemAtPosition(position);


        //Log.i("Item", String.valueOf(resid));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class LineChartReportTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "";
            if(voids[0].equalsIgnoreCase("hourly")){
                url1 = "com.project.entities.electricityusage/findDailyorHourlyUsageofAllAppliances/" + voids[1] +"/" + voids[2] + "/" + voids[0];
            } else if(voids[0].equalsIgnoreCase("daily")){
                url1 = "com.project.entities.electricityusage/findMonthUsageofAllAppliancesOfResident/" + voids[1] +"/03";
            } else {
                url1 = "com.project.entities.electricityusage/findDailyorHourlyUsageofAllAppliances/" + voids[1] +"/" + voids[2] + "/" + voids[0];
            }

            HttpURLConnection urlConnection = RESTConnection.createConnection(url1);
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
                addDataSet(value);

            }else{

            }



        }
    }


    public static class LineGraphDatePickerFragment extends DialogFragment
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
