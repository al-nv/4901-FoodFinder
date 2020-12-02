package com.example.onlinestore.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.FragmentMainActivity;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
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
    LinearLayout spinnerLayout;
    EditText nameEdt,phoneEdt,emailEdt,addressEdt,stateEdt,cityEdt,zipEdt,passwordEdt,cnfpasswordEdt;
    String username,phoneNo,email,address,state,city,zip,password,cnfpasswor,userType;
    // Array of choices
    String usertype[] = {"User","Seller"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        spinnerLayout = findViewById(R.id.spinnerLayout);
        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        nameEdt = findViewById(R.id.nameEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        emailEdt = findViewById(R.id.emailEdt);
        addressEdt = findViewById(R.id.addressEdt);
        stateEdt = findViewById(R.id.stateEdt);
        cityEdt = findViewById(R.id.cityEdt);
        zipEdt = findViewById(R.id.zipEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        cnfpasswordEdt = findViewById(R.id.cnfpasswordEdt);
        spinnerType = findViewById(R.id.spinnerType);
        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, usertype);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerType.setAdapter(spinnerArrayAdapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                userType =String.valueOf(adapterView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(getIntent().getStringExtra("seller") != null) {
            if (getIntent().getStringExtra("seller").equals("seller")) {
                nameEdt.setText(userDetails.get(_USERNAME));
                emailEdt.setText(userDetails.get(_EMAIL));
                phoneEdt.setText(userDetails.get(_PHONENO));
                addressEdt.setText(userDetails.get(_ADDRESS));
                stateEdt.setText(userDetails.get(_STATE));
                cityEdt.setText(userDetails.get(_CITY));
                zipEdt.setText(userDetails.get(_ZIP));
                passwordEdt.setVisibility(View.GONE);
                cnfpasswordEdt.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                getSupportActionBar().setTitle("Seller Update");
                submitBtn.setText("Update Seller");
            }
        }else if(getIntent().getStringExtra("user") != null) {
            if (getIntent().getStringExtra("user").equals("user")) {
                nameEdt.setText(userDetails.get(_USERNAME));
                emailEdt.setText(userDetails.get(_EMAIL));
                phoneEdt.setText(userDetails.get(_PHONENO));
                addressEdt.setText(userDetails.get(_ADDRESS));
                stateEdt.setText(userDetails.get(_STATE));
                cityEdt.setText(userDetails.get(_CITY));
                zipEdt.setText(userDetails.get(_ZIP));
                passwordEdt.setVisibility(View.GONE);
                cnfpasswordEdt.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                getSupportActionBar().setTitle("User Update");
                submitBtn.setText("Update User");
            }
        }
    }
    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.submitBtn){
            username = nameEdt.getText().toString().trim();
            phoneNo = phoneEdt.getText().toString().trim();
            email = emailEdt.getText().toString().trim();
            address = addressEdt.getText().toString().trim();
            state = stateEdt.getText().toString().trim();
            city = cityEdt.getText().toString().trim();
            zip = zipEdt.getText().toString().trim();
            password = passwordEdt.getText().toString().trim();
            cnfpasswor = cnfpasswordEdt.getText().toString().trim();

            builder = new AlertDialog.Builder(RegisterActivity.this);
            if(username.equals("") || email.equals("") || address.equals("") || state.equals("") || city.equals("") || zip.equals("")){
                builder.setTitle("Something went wrong.....");
                builder.setMessage("Please fill all the fields...");
                displayAlert("input_error");
            }
            else
            {
                if(getIntent().getStringExtra("seller") != null) {
                    if (getIntent().getStringExtra("seller").equals("seller")) {
                        updateregister();
                    }
                }else if(getIntent().getStringExtra("user") != null) {
                    if (getIntent().getStringExtra("user").equals("user")) {
                        updateregister();
                    }
                }else {
                    if (!(password.equals(cnfpasswor))) {
                        builder.setTitle("Something went wrong.....");
                        builder.setMessage("Your password not matching...");
                        displayAlert("input_error");
                    } else {
                        register();
                    }
                }
            }
        }else if(id == R.id.cancelBtn){
            if(getIntent().getStringExtra("seller") != null) {
                if (getIntent().getStringExtra("seller").equals("seller")) {
                    Intent intent = new Intent(RegisterActivity.this, FragmentMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else if(getIntent().getStringExtra("user") != null) {
                if (getIntent().getStringExtra("user").equals("user")) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    public void register(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Register ...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://192.168.56.1/registerOnline.php";
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
                Toast.makeText(RegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("username",username);
                param.put("phoneNo",phoneNo);
                param.put("email",email);
                param.put("address",address);
                param.put("state",state);
                param.put("city",city);
                param.put("zip",zip);
                param.put("password",password);
                param.put("type",userType);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(requestRegister);
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
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if(code.equals("no_success")){
                    progressView.dismiss();
                    nameEdt.setText("");
                    passwordEdt.setText("");
                    emailEdt.setText("");
                    addressEdt.setText("");
                    stateEdt.setText("");
                    cityEdt.setText("");
                    zipEdt.setText("");
                    passwordEdt.setText("");
                    cnfpasswordEdt.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateregister(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Updating...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://192.168.56.1/updateSellerProfile.php";
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
                Toast.makeText(RegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                param.put("username",String.valueOf(userDetails.get(_USERNAME)));
                param.put("phoneNo",phoneNo);
                param.put("email",email);
                param.put("address",address);
                param.put("state",state);
                param.put("city",city);
                param.put("zip",zip);
                param.put("registerId",String.valueOf(userDetails.get(_REGISTERID)));
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(requestRegister);
    }
}
