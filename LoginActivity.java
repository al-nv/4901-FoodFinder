package com.example.onlinestore.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.FragmentMainActivity;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.gps.Globle;
import com.example.onlinestore.gps.GpsLocationReceiver;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_RUNTIME_PERMISSIONS = 105;
    private static final String _REGISTERID = "registerid";
    private static final String _TYPE = "usertype";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    private Button loginBtn,registerbtn;
    EditText usernameEdt,passwordEdt;
    TextView userRegistrationTxt,storeRegistrationTxt;
    String username,password;
    AlertDialog.Builder builder;
    GpsLocationReceiver gpsLocationReceiver;
    Globle internetCheck;

    public static void requestLocationPerm() {
        LoginActivity activity = new LoginActivity();
        ActivityCompat.requestPermissions(activity, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION
        }, 50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // getSupportActionBar().hide();
        usernameEdt = findViewById(R.id.edt_username);
        passwordEdt = findViewById(R.id.edt_password);
        loginBtn = findViewById(R.id.loginbtn);
        registerbtn = findViewById(R.id.registerbtn);

        checkRuntimePermissions();
        internetCheck = new Globle(this);
        //gps
        gpsLocationReceiver = new GpsLocationReceiver();
        registerReceiver(gpsLocationReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        registerReceiver(gpsLocationReceiver, new IntentFilter("oms.gps.status.check"));
        //shared preferences
        userPreference = new UserPreference(getApplicationContext());
        userDetails = userPreference.getUser();
        loginBtn.setOnClickListener(this);


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, UserStoreRegisterActivity.class);
                startActivity(i);
            }
        });


        if(userDetails.get(_TYPE) != null) {
            if (userDetails.get(_TYPE).equals("user")) {
                if (userPreference.isLoggedIn()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            } else if (userDetails.get(_TYPE).equals("store")) {
                if (userPreference.isLoggedIn()) {
                    Intent i = new Intent(LoginActivity.this, FragmentMainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.loginbtn){

            username = usernameEdt.getText().toString().trim();
            password = passwordEdt.getText().toString().trim();
            builder = new AlertDialog.Builder(LoginActivity.this);
            if(username.equals("") || password.equals("")){
                builder.setTitle("Something went wrong.....");
                displayAlert("Enter a valid username and password...");
            }
            else
            {
                if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                    Login();
                }else {
                    Toast.makeText(LoginActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void Login() {
        final ProgressDialog progressView = new ProgressDialog(this);
        progressView.setMessage("Logging user...");
        progressView.setCancelable(false);
        progressView.show();
        String url = "http://192.168.56.1/loginOnline.php";
        StringRequest requestLogin = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String UserName ="null";
                            String StoreName ="null";
                            String phoneNo ="null";
                            String email ="null";
                            String address ="null";
                            String state ="null";
                            String city ="null";
                            String zip ="null";
                            String UserId ="null";
                            String StoreId ="null";
                            String UserType ="null";
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if(code.equals("login_failed")){
                                builder.setTitle("Login Error...");
                                displayAlert(jsonObject.getString("message"));
                                progressView.dismiss();
                                //   Toast.makeText(LoginActivity.this, "faild", Toast.LENGTH_SHORT).show();
                            }
                            else if(code.equals("login_success"))
                            {
                                UserType = jsonObject.getString("UserType");
                                userPreference.userType(UserType);
                                userPreference.setLo(true);
                                if(UserType.equals("user")){
                                    UserId = jsonObject.getString("UserId");
                                    UserName = jsonObject.getString("FullName");
                                    userPreference.saveRegisterId(UserId);
                                    userPreference.saveUserName(UserName);
                                    // Toast.makeText(LoginActivity.this, ""+UserName, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else if(UserType.equals("store")) {
                                    StoreId = jsonObject.getString("StoreId");
                                    StoreName = jsonObject.getString("StoreName");
                                    userPreference.saveStoreId(StoreId);
                                    userPreference.saveUserName(StoreName);
                                    // Toast.makeText(LoginActivity.this, ""+StoreName, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, FragmentMainActivity.class);
                                    startActivity(intent);
                                }
                                progressView.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressView.dismiss();
                Toast.makeText(LoginActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("username",username);
                param.put("password",password);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestLogin.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestLogin);
    }

    public void displayAlert(final String message){
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                usernameEdt.setText("");
                passwordEdt.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void checkRuntimePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int fineLocationPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocationPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            int readPhoneStatePerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (fineLocationPerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (coarseLocationPerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (readPhoneStatePerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_RUNTIME_PERMISSIONS);
            }
        }
    }
}
