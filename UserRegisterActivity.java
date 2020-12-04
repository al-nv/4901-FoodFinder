package com.example.onlinestore.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.gps.Globle;
import com.example.onlinestore.model.UserModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String _REGISTERID = "registerid";
    private static final String _USERNAME = "username";
    private static final String _PHONENO = "phoneNo";
    private static final String _EMAIL = "email";
    private static final String _ADDRESS = "address";
    private static final String _STATE = "state";
    private static final String _CITY = "city";
    private static final String _ZIP = "zip";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    Button submitBtn,cancelBtn;
    Spinner spinnerType;
    GPSTracker gpsTracker;
    LinearLayout spinnerLayout;
    EditText fullNameEdt,nameEdt,phoneEdt,mobileNoEdt,emailEdt,addressEdt,stateEdt,cityEdt,zipEdt,passwordEdt,cnfpasswordEdt;
    String FullName,UserName,PhoneNo,MobileNo,Email,Address,State,City,ZipCode,Password,cnfpasswor,Latitude,Longitude,Status;
    // Array of choices
    String usertype[] = {"User","Seller"};
    Globle internetCheck;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Register");

        internetCheck = new Globle(this);
        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        fullNameEdt = findViewById(R.id.fullNameEdt);
        nameEdt = findViewById(R.id.nameEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        mobileNoEdt = findViewById(R.id.mobileNoEdt);
        emailEdt = findViewById(R.id.emailEdt);
        addressEdt = findViewById(R.id.addressEdt);
        stateEdt = findViewById(R.id.stateEdt);
        cityEdt = findViewById(R.id.cityEdt);
        zipEdt = findViewById(R.id.zipEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        cnfpasswordEdt = findViewById(R.id.cnfpasswordEdt);

        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        if(getIntent().getStringExtra("user") != null) {
            if (getIntent().getStringExtra("user").equals("user")) {
                nameEdt.setVisibility(View.GONE);
                passwordEdt.setVisibility(View.GONE);
                cnfpasswordEdt.setVisibility(View.GONE);
                getSupportActionBar().setTitle("User Update");
                submitBtn.setText("Update User");

                if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                    fetchUserProfile();
                }else {
                    Toast.makeText(UserRegisterActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.submitBtn){
            FullName = fullNameEdt.getText().toString().trim();
            UserName = nameEdt.getText().toString().trim();
            PhoneNo = phoneEdt.getText().toString().trim();
            MobileNo = mobileNoEdt.getText().toString().trim();
            Email = emailEdt.getText().toString().trim();
            Address = addressEdt.getText().toString().trim();
            State = stateEdt.getText().toString().trim();
            City = cityEdt.getText().toString().trim();
            ZipCode = zipEdt.getText().toString().trim();
            Password = passwordEdt.getText().toString().trim();
            cnfpasswor = cnfpasswordEdt.getText().toString().trim();

            builder = new AlertDialog.Builder(UserRegisterActivity.this);
            if(FullName.equals("") || Email.equals("") || Address.equals("") || State.equals("") || City.equals("") || PhoneNo.equals("")  || ZipCode.equals("")){
                builder.setTitle("Something went wrong.....");
                builder.setMessage("Please fill all the fields...");
                displayAlert("input_error");
            }
            else
            {
                if(getIntent().getStringExtra("user") != null) {
                    if (getIntent().getStringExtra("user").equals("user")) {
                        updateregister();
                    }
                }else {
                    if (!(Password.equals(cnfpasswor))) {
                        builder.setTitle("Something went wrong.....");
                        builder.setMessage("Your password not matching...");
                        displayAlert("input_error");
                    } else if (!isEmailValidate(emailEdt.getText().toString().trim())) {

                    }else if (!isValidNumber(passwordEdt.getText().toString().trim())) {

                    }else if (isValidPassword(passwordEdt.getText().toString().trim())) {

                        if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                       UserRegister();
                        }else {
                            Toast.makeText(UserRegisterActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(UserRegisterActivity.this, "InValid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if(id == R.id.cancelBtn){
            Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void UserRegister(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Register user...");
        progressView.setCancelable(false);
        progressView.show();
        String urlUserRegister = "http://192.168.56.1/UserRegister.php";
        StringRequest requestUserRegister = new StringRequest(Request.Method.POST,urlUserRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            builder.setTitle("Server Response....");
                            builder.setMessage(message);
                            displayAlert(code);
                            // profileImage.setImageResource(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.dismiss();
                Toast.makeText(UserRegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("FullName",FullName);
                param.put("UserName",UserName);
                param.put("PhoneNo",PhoneNo);
                param.put("MobileNo",MobileNo);
                param.put("Email",Email);
                param.put("Address",Address);
                param.put("State",State);
                param.put("City",City);
                param.put("ZipCode",ZipCode);
                param.put("Password",Password);
                param.put("Latitude",String.valueOf(gpsTracker.getLatitude()));
                param.put("Longitude",String.valueOf(gpsTracker.getLongitude()));
                param.put("Status","1");
                param.put("UserType","user");
                return param;
            }
        };
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestUserRegister.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestUserRegister);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void displayAlert(final String code){
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    progressView.dismiss();
                    Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(code.equals("no_success")){
                    progressView.dismiss();
                    fullNameEdt.setText("");
                    nameEdt.setText("");
                    passwordEdt.setText("");
                    emailEdt.setText("");
                    phoneEdt.setText("");
                    mobileNoEdt.setText("");
                    addressEdt.setText("");
                    stateEdt.setText("");
                    cityEdt.setText("");
                    zipEdt.setText("");
                    passwordEdt.setText("");
                    cnfpasswordEdt.setText("");
                }else if(code.equals("reg_failed")) {

                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void fetchUserProfile(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("fetching user profile...");
        progressView.setCancelable(false);
        progressView.show();
        String urlFetchStore = "http://192.168.56.1/FetchUserProfile.php";
        StringRequest requestFetchStor = new StringRequest(Request.Method.POST,urlFetchStore,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            //  String message = jsonObject.getString("message");
                            if(code.equals("profile_success")) {
                                //FullName,PhoneNo,MobileNo,Email,Address,State,City,ZipCode,Password,Latitude,Longitude,Status,UserType
                                UserModel info = new UserModel();
                                info.setFullName(jsonObject.getString("FullName"));
                                info.setPhoneNo(jsonObject.getString("PhoneNo"));
                                info.setMobileNo(jsonObject.getString("MobileNo"));
                                info.setEmail(jsonObject.getString("Email"));
                                info.setAddress(jsonObject.getString("Address"));
                                info.setState(jsonObject.getString("State"));
                                info.setCity(jsonObject.getString("City"));
                                info.setZipCode(jsonObject.getString("ZipCode"));


                                fullNameEdt.setText(info.getFullName());
                                phoneEdt.setText(info.getPhoneNo());
                                mobileNoEdt.setText(info.getMobileNo());
                                emailEdt.setText(info.getEmail());
                                addressEdt.setText(info.getAddress());
                                stateEdt.setText(info.getState());
                                cityEdt.setText(info.getCity());
                                zipEdt.setText(info.getZipCode());

                            }
                            //;
                            // profileImage.setImageResource(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.dismiss();
                Toast.makeText(UserRegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                param.put("UserId",String.valueOf(userDetails.get(_REGISTERID)));
                return param;
            }
        };
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestFetchStor.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestFetchStor);
    }

    public void updateregister(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Updating user profile...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://192.168.56.1/UpdateUserRegisterProfile.php";
        StringRequest requestRegister = new StringRequest(Request.Method.POST,urlRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            builder.setTitle("Server Response....");
                            builder.setMessage(message);
                            displayAlert(code);
                            // profileImage.setImageResource(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.dismiss();
                Toast.makeText(UserRegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                //FullName,PhoneNo,MobileNo,Email,Address,State,City,ZipCode,Latitude,Longitude
                param.put("FullName",FullName);
                param.put("PhoneNo",PhoneNo);
                param.put("MobileNo",MobileNo);
                param.put("Email",Email);
                param.put("Address",Address);
                param.put("State",State);
                param.put("City",City);
                param.put("ZipCode",ZipCode);
                param.put("Latitude",String.valueOf(gpsTracker.getLatitude()));
                param.put("Longitude",String.valueOf(gpsTracker.getLongitude()));
                param.put("UserId",String.valueOf(userDetails.get(_REGISTERID)));
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(UserRegisterActivity.this);
        queue.add(requestRegister);
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    public boolean isValidNumber(final String password){
        boolean valid = false;
        if(password.length() >= 8){
            valid = true;

        }else {
            valid = false;
            Toast.makeText(UserRegisterActivity.this, "Please enter max password lenth 8", Toast.LENGTH_SHORT).show();
        }
        return valid;

    }

    public boolean isEmailValidate(final String password){
        boolean valid = false;
        if (password.matches(emailPattern)) {
            valid = true;
        } else {
            valid = false;
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }
}
