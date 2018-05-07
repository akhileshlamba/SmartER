package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.DatePicker;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText postcode;
    EditText userName;
    EditText email;
    EditText pwd;
    EditText cpwd;
    EditText contact;
    EditText address;
    Spinner energy;
    TextView dob;
    Spinner spinner;
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.pwd);
        contact = (EditText) findViewById(R.id.contact);
        address = (EditText) findViewById(R.id.address);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        postcode = (EditText) findViewById(R.id.postcode);
        dob = (TextView) findViewById(R.id.dateTv);
        cpwd = (EditText) findViewById(R.id.cPassword);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.occupants, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        energy = (Spinner) findViewById(R.id.energy);
        ArrayAdapter<CharSequence> energyAdapter = ArrayAdapter.createFromResource(this,
                R.array.energyprovider, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        energy.setAdapter(energyAdapter);

    }

    public void pickDate(View view){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DATE);

        DatePickerDialog pickerDialog = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
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
                dob.setText(dateSet);
            }
        }, year, month, dayOfMonth);
        pickerDialog.show();
    }

    public void createUser(View view){

        String username = userName.getText().toString();
        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();
        String pword = pwd.getText().toString();
        String cpword = cpwd.getText().toString();
        String emailId = email.getText().toString();
        String phone = contact.getText().toString();
        String code = postcode.getText().toString();
        String dateOfBirth = dob.getText().toString();
        String energyProvider = energy.getSelectedItem().toString();
        String add = address.getText().toString();
        String occupants = spinner.getSelectedItem().toString();
        String pwdd = BCrypt.hashpw(pword, BCrypt.gensalt());

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pword);
        if(pword.length() < 8 || pword.length() > 15)
            Toast.makeText(this,"Password length should be between 8 and 15", Toast.LENGTH_LONG).show();
        else
        if(!matcher.matches())
            Toast.makeText(this,"Password should contain at least 1 uppercase, 1 lowercase, 1 numeric and 1 special character", Toast.LENGTH_LONG).show();

        if(!cpword.equals(pword))
            Toast.makeText(this,"Password and confirm password should match", Toast.LENGTH_SHORT).show();

        if(code.length() != 4)
            Toast.makeText(this,"Postcode should be number and of 4 digits", Toast.LENGTH_SHORT).show();

        if(phone.length() != 9)
            Toast.makeText(this,"Phone number should be of 9 digits", Toast.LENGTH_SHORT).show();

        if(username.length() == 0 || firstname.length() == 0 || lastname.length() == 0 ||
                emailId.length() == 0 || dateOfBirth.length() == 0 ||
                energyProvider.length() == 0 || add.length() == 0 || occupants.length() == 0) {
            Toast.makeText(this,"No field should be empty", Toast.LENGTH_SHORT).show();
        } else {

            String[] data = {username, firstname, lastname, pwdd, emailId, phone, code, dateOfBirth, energyProvider, add, occupants};
            new RegistrationTask().execute(data);
        }




    }


    private class RegistrationTask extends AsyncTask<String, Void, Boolean> {
        boolean create = false;

        @Override
        protected Boolean doInBackground(String... voids) {
            String url1 = "com.project.entities.house/createUser";
            boolean emailFlag = false;
            String emailURL = "com.project.entities.house/findByEmail";
            HttpURLConnection emailUrlConnection = RESTConnection.createConnection(emailURL+"/" +voids[4].toString());
            try{
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(emailUrlConnection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONArray data = new JSONArray(json.toString());
                Log.i("JSON", json.toString());
                if(!json.toString().equals("[]\n")){
                    emailFlag = true;
                }else
                    emailFlag = false;
            }catch(Exception e){
                Log.e("EmailException", e.toString());
                emailFlag = true;
            }

            boolean userNameFlag = false;
            String userNamelURL = "com.project.entities.rescredientials/findByUserName";
            HttpURLConnection userNameUrlConnection = RESTConnection.createConnection(userNamelURL+"/" +voids[0].toString());
            try{
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(userNameUrlConnection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONArray data = new JSONArray(json.toString());
                Log.i("JSON", json.toString());
                if(!json.toString().equals("[]\n")){
                    userNameFlag = true;
                }

                else{
                    userNameFlag = false;
                }
            }catch(Exception e){
                Log.e("UsernameException", e.toString());
                userNameFlag = true;
            }

            if(!emailFlag && !userNameFlag){
                HttpURLConnection urlConnection = RESTConnection.createConnection(url1);
                try{
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    JSONObject object = new JSONObject();
                    object.put("username", voids[0].toString());
                    object.put("firstname", voids[1].toString());
                    object.put("lastname", voids[2].toString());
                    object.put("pwd", voids[3].toString());
                    object.put("emailId", voids[4].toString());
                    object.put("phone", Integer.valueOf(voids[5]));
                    object.put("code", Integer.valueOf(voids[6]));
                    object.put("dateOfBirth", voids[7].toString());
                    object.put("energyProvider", voids[8].toString());
                    object.put("add", voids[9].toString());
                    object.put("occupants", Integer.valueOf(voids[10]));

                    OutputStreamWriter wr= new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");

                    wr.write(object.toString());
                    wr.flush();
                    wr.close();

                    Log.i("URLCOnnection", urlConnection.toString());

                    int code = urlConnection.getResponseCode();
                    Log.i("code", String.valueOf(code));
                    return true;

                }catch(Exception e){
                    Log.e("Exception", e.toString());
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean create) {
            if(create){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Toast.makeText(getApplicationContext(), "User registered", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }


}
