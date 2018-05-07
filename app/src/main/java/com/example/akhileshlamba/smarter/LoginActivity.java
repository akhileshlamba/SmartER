package com.example.akhileshlamba.smarter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.akhileshlamba.smarter.entities.House;
import com.example.akhileshlamba.smarter.entities.ResCredientials;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {


    EditText userNameTv;
    EditText passwordTv;

    public void newUser(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void validate(View view) {
        if(userNameTv.getText().toString().length() == 0)
            Toast.makeText(this, "Enter Username", Toast.LENGTH_LONG).show();
        else if(passwordTv.getText().toString().length() == 0)
                Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
        else
            new Login().execute("", null , null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameTv = (EditText) findViewById(R.id.username);
        passwordTv = (EditText) findViewById(R.id.pwd);


    }



    private class Login extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... voids) {
            String url1 = "com.project.entities.rescredientials/validateUser/";
            JSONArray array = RESTConnection.validateUser(RESTConnection.createConnection(url1 + userNameTv.getText().toString()));
            return array;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray.length() != 0) {
                try{
                    if(BCrypt.checkpw(passwordTv.getText().toString(), jsonArray.getJSONObject(0).getString("password"))) {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();

                        JSONObject object = jsonArray.getJSONObject(0);

                        JSONObject resident = object.getJSONObject("resid");
                        Log.i("Resident", resident.toString());


                        ResCredientials resCredientials = new ResCredientials();
                        resCredientials.setPassword(object.getString("password"));
                        resCredientials.setUserName(object.getString("userName"));
                        resCredientials.setRegDate(new SimpleDateFormat("yyyy-MM-dd").parse(object.getString("regDate")));
                        resCredientials.setResid(resident.getInt("resid"));

                        House house = new House();
                        house.setAddress(resident.getString("address"));
                        house.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(resident.getString("dob")));
                        house.setEmail(resident.getString("email"));
                        house.setFirstName(resident.getString("firstName"));
                        house.setLastName(resident.getString("lastName"));
                        house.setEnergyproviderName(resident.getString("energyproviderName"));
                        house.setMobile(resident.getLong("mobile"));
                        house.setNoOfOccupants(resident.getInt("noOfOccupants"));
                        house.setPostcode(resident.getInt("postcode"));
                        house.setResid(resident.getInt("resid"));
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("house", house);
                        bundle.putParcelable("credientials", resCredientials);
                        intent.putExtra("profile", bundle);
                        startActivity(intent);
                        finish();
                    }else
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Log.e("Exception", e.toString());
                }
            }else
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();

            super.onPostExecute(jsonArray);
        }
    }

}
