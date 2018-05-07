package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;


public class BarGraphFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private static String TAG = "BarChart";
    Spinner spinner;
    JSONArray json;
    ArrayList<Float> data = new ArrayList<>();
    BarChart barChart;
    private String value;
    View fragmentBarGraph;
    private int resid=0;
    private static String date = "";
    private static TextView datePie;
    private Button pickDate;
    private Button setButton;

    public BarGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentBarGraph =  inflater.inflate(R.layout.fragment_bar_graph, container, false);

        resid = getArguments().getInt("resid");

        datePie = (TextView) fragmentBarGraph.findViewById(R.id.tDate);
        pickDate = (Button) fragmentBarGraph.findViewById(R.id.pBDate);
        setButton = (Button) fragmentBarGraph.findViewById(R.id.vBReport);


        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new BarGraphDatePickerFragment();

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });


        spinner = (Spinner) fragmentBarGraph.findViewById(R.id.barPicker);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.usageType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        barChart = (BarChart) fragmentBarGraph.findViewById(R.id.barChart);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] voids = {spinner.getSelectedItem().toString(), String.valueOf(resid), datePie.getText().toString()};
                if(datePie.getText().equals("Date") && spinner.getSelectedItem().toString().equals("Hourly"))
                    Toast.makeText(getActivity().getApplicationContext(), "Please select date to view Report", Toast.LENGTH_LONG).show();
                else
                    new BarGraphReportTask().execute(voids);
            }
        });

        return  fragmentBarGraph;
    }


    private void addDataSet(String view) {
        Log.d(TAG, "addDataSet started");
        ArrayList<BarEntry> entrys = new ArrayList<>();
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
            if(!data.isEmpty()){
                data = new ArrayList<>();
            }
            for (int i = 0; i<json.length(); i++){
                JSONObject object = json.getJSONObject(i);
                data.add((float) object.getDouble("usage"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for( int i =0; i< data.size(); i++)
            entrys.add(new BarEntry(i, data.get(i)));


        //create the data set
        BarDataSet barDataSet = new BarDataSet(entrys, "Electricity Usage");
        BarData barData = new BarData(barDataSet);

        barData.setBarWidth(0.4f);

        barChart.setData(barData);
        Description description = new Description();
        description.setText("Usage");
        barChart.setDescription(description);
        XAxis xAxisFromChart = barChart.getXAxis();
        xAxisFromChart.setValueFormatter(formatter);
        barChart.invalidate();
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
                for (int i = 0; i< data.size(); i++) {
                    index = data.indexOf(Float.valueOf(usage));
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class BarGraphReportTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "";
            if(voids[0].equalsIgnoreCase("hourly")){
                url1 = "com.project.entities.electricityusage/findHourlyUsageofAllAppliancesOfResident/" + voids[1] + "/" +voids[2];
            } else if(voids[0].equalsIgnoreCase("daily")){
                url1 = "com.project.entities.electricityusage/findMonthUsageofAllAppliancesOfResident/" + voids[1] +"/03";
            } else {
                url1 = "com.project.entities.electricityusage/findHourlyUsageofAllAppliancesOfResident/" + voids[1] + "/" +voids[2];
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

    public static class BarGraphDatePickerFragment extends DialogFragment
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
