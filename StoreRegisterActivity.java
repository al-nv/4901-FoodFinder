package com.example.onlinestore.activity;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.onlinestore.FragmentMainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.gps.Globle;
import com.example.onlinestore.model.StoreModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//+61410441726
public class StoreRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String _REGISTERID = "registerid";
    private static final String _USERNAME = "username";
    private static final String _PHONENO = "phoneNo";
    private static final String _EMAIL = "email";
    private static final String _ADDRESS = "address";
    private static final String _STATE = "state";
    private static final String _CITY = "city";
    private static final String _ZIP = "zip";
    private static final String _STOREID = "storeId";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    Button submitBtn, cancelBtn;
    Spinner spinnerType;
    GPSTracker gpsTracker;
    String urlImage;
    LinearLayout spinnerLayout;
    TextView storeImageTxt,tvPasswordStrength;
    ImageView storeImageView;
    EditText storeNameEdt, addressEdt, phoneEdt, mobileNoEdt, emailEdt, openingHourEdt, closrouEdt, stateEdt, userNameEdt, passwordEdt, cnfpasswordEdt;
    String StoreName, UserName, Address, PhoneNo, MobileNo, Email, OpeningHour, ClosHour, State, City, ZipCode, Password, cnfpasswor, Latitude, Longitude, Status;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    Globle internetCheck;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //app_key 56e53808-fe83-4174-8aef-ac507a7e5b75
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Store Register");


        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        storeImageTxt = findViewById(R.id.storeImageTxt);
        storeImageView = findViewById(R.id.storeImageView);
        storeNameEdt = findViewById(R.id.storeNameEdt);
        addressEdt = findViewById(R.id.addressEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        mobileNoEdt = findViewById(R.id.mobileNoEdt);
        emailEdt = findViewById(R.id.emailEdt);
        openingHourEdt = findViewById(R.id.openingHourEdt);
        closrouEdt = findViewById(R.id.closrouEdt);
        stateEdt = findViewById(R.id.stateEdt);
        userNameEdt = findViewById(R.id.userNameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        cnfpasswordEdt = findViewById(R.id.cnfpasswordEdt);
        internetCheck = new Globle(this);
        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        openingHourEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        openingHourEdt.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(StoreRegisterActivity.this, onTimeSetListener, hour, minute, is24Hour);
                timePickerDialog.show();
            }
        });

        closrouEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        closrouEdt.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(StoreRegisterActivity.this, onTimeSetListener, hour, minute, is24Hour);
                timePickerDialog.show();
            }
        });

        if (getIntent().getStringExtra("store") != null) {
            if (getIntent().getStringExtra("store").equals("store")) {
                userNameEdt.setVisibility(View.GONE);
                passwordEdt.setVisibility(View.GONE);
                cnfpasswordEdt.setVisibility(View.GONE);
                getSupportActionBar().setTitle("Store Update");
                submitBtn.setText("Update Store");
                if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                    fetchStoreProfile();
                }else {
                    Toast.makeText(StoreRegisterActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                }

            }
        }

        storeImageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.submitBtn) {
            StoreName = storeNameEdt.getText().toString().trim();
            PhoneNo = phoneEdt.getText().toString().trim();
            MobileNo = mobileNoEdt.getText().toString().trim();
            Email = emailEdt.getText().toString().trim();
            Address = addressEdt.getText().toString().trim();
            OpeningHour = openingHourEdt.getText().toString().trim();
            ClosHour = closrouEdt.getText().toString().trim();
            UserName = userNameEdt.getText().toString().trim();
            Password = passwordEdt.getText().toString().trim();
            cnfpasswor = cnfpasswordEdt.getText().toString().trim();

            builder = new AlertDialog.Builder(StoreRegisterActivity.this);
            if (StoreName.equals("") || Email.equals("") || Address.equals("") || PhoneNo.equals("") || OpeningHour.equals("") || ClosHour.equals("") || MobileNo.equals("")) {
                builder.setTitle("Something went wrong.....");
                builder.setMessage("Please fill all the fields...");
                displayAlert("input_error");
            } else {

                if (getIntent().getStringExtra("store") != null) {
                    if (getIntent().getStringExtra("store").equals("store")) {
                        builder.setTitle("Server Response....");
                        builder.setMessage("Are you sure want to update store");
                        displaypdateStore("reg_success");

                    }
                } else {
                    if (!(Password.equals(cnfpasswor))) {
                        builder.setTitle("Something went wrong.....");
                        builder.setMessage("Your password not matching...");
                        displayAlert("input_error");
                    } else if (!isEmailValidate(emailEdt.getText().toString().trim())) {

                    } else if (!isValidNumber(passwordEdt.getText().toString().trim())) {

                    } else if (isValidPassword(passwordEdt.getText().toString().trim())) {

                        if (internetCheck.getConnectivityStatus(getApplicationContext())) {
                            storeRegister();
                        } else {
                            Toast.makeText(StoreRegisterActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(StoreRegisterActivity.this, "InValid Password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        } else if (id == R.id.cancelBtn) {
            Intent intent = new Intent(StoreRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void storeRegister() {
        progressView = new ProgressDialog(this);
        progressView.setMessage("Store register ...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://192.168.56.1/StoreRegister.php";
        StringRequest requestRegister = new StringRequest(Request.Method.POST, urlRegister,
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
                Toast.makeText(StoreRegisterActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {//StoreName,Address,PhoneNo,MobileNo,EmailId,OpeningHour,CloseHour,StoreImage,Latitude,Longitude,
            // Status,UserName,Password
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                param.put("StoreName", StoreName);
                param.put("Address", Address);
                param.put("PhoneNo", PhoneNo);
                param.put("MobileNo", MobileNo);
                param.put("EmailId", Email);
                param.put("OpeningHour", OpeningHour);
                param.put("CloseHour", ClosHour);
                param.put("StoreImage", ts);
                param.put("UserName", UserName);
                param.put("Password", Password);
                param.put("name", imageToString(bitmap));
                param.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                param.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                param.put("Status", "1");
                param.put("UserType", "store");
                return param;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestRegister.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestRegister);

    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (code.equals("reg_success")) {
                    progressView.dismiss();
                    Intent intent = new Intent(StoreRegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (code.equals("no_success")) {
                    progressView.dismiss();
                    storeNameEdt.setText("");
                    openingHourEdt.setText("");
                    passwordEdt.setText("");
                    emailEdt.setText("");
                    phoneEdt.setText("");
                    mobileNoEdt.setText("");
                    addressEdt.setText("");
                    closrouEdt.setText("");
                    userNameEdt.setText("");
                    passwordEdt.setText("");
                    cnfpasswordEdt.setText("");
                } else if (code.equals("reg_failed")) {

                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void displaypdateStore(final String code) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (code.equals("reg_success")) {
                    progressView.dismiss();
                    if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                        updateStoreregister();
                    }else {
                        Toast.makeText(StoreRegisterActivity.this, "No internet available....", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressView.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                storeImageView.setImageResource(0);
                storeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public void fetchStoreProfile(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Fetching store profile...");
        progressView.setCancelable(false);
        progressView.show();
        String urlFetchStore = "http://192.168.56.1/FetchStoreProfile.php";
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
                                StoreModel info = new StoreModel();
                                info.setStoraName(jsonObject.getString("StoreName"));
                                info.setAddress(jsonObject.getString("Address"));
                                info.setPhoneNo(jsonObject.getString("PhoneNo"));
                                info.setMobileNo(jsonObject.getString("MobileNo"));
                                info.setEmail(jsonObject.getString("EmailId"));
                                info.setOperationHour(jsonObject.getString("OpeningHour"));
                                info.setCloseHour(jsonObject.getString("CloseHour"));
                                info.setStoreImage(jsonObject.getString("StoreImage"));
                                storeNameEdt.setText(info.getStoraName());
                                addressEdt.setText(info.getAddress());
                                phoneEdt.setText(info.getPhoneNo());
                                mobileNoEdt.setText(info.getMobileNo());
                                emailEdt.setText(info.getEmail());
                                openingHourEdt.setText(info.getOperationHour());
                                closrouEdt.setText(info.getCloseHour());
                                Glide.with(StoreRegisterActivity.this)
                                        .load("http://192.168.56.1/storeImage/" + info.getStoreImage() + ".jpg")
                                        .placeholder(R.drawable.ic_broken)
                                        .into(storeImageView);
                                urlImage = "http://192.168.56.1/storeImage/"+info.getStoreImage()+".jpg";
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
                Toast.makeText(StoreRegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                param.put("StoreId",String.valueOf(userDetails.get(_STOREID)));
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
    public void updateStoreregister(){
        progressView = new ProgressDialog(this);
        progressView.setMessage("Updating store profile...");
        progressView.setCancelable(false);
        progressView.show();
        String urlStoreUpdate = "http://192.168.56.1/UpdateStoreRegisterProfile.php";
        StringRequest requestStoreUpdate = new StringRequest(Request.Method.POST,urlStoreUpdate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(StoreRegisterActivity.this, FragmentMainActivity.class);
                            startActivity(intent);
                            finish();
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
                Toast.makeText(StoreRegisterActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                //StoreName,Address,PhoneNo,MobileNo,EmailId,OpeningHour,CloseHour,StoreImage,Status,UserType
                param.put("StoreName",StoreName);
                param.put("Address",Address);
                param.put("PhoneNo",PhoneNo);
                param.put("MobileNo",MobileNo);
                param.put("EmailId",Email);
                param.put("OpeningHour",OpeningHour);
                param.put("CloseHour",ClosHour);
                param.put("StoreImage", ts);

                if(bitmap == null){
                    param.put("name", imageToString(getBitmapFromURL(urlImage)));
                }else {
                    param.put("name", imageToString(bitmap));
                }
                param.put("StoreId",String.valueOf(userDetails.get(_STOREID)));
                return param;
            }
        };
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestStoreUpdate.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestStoreUpdate);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            Toast.makeText(StoreRegisterActivity.this, "Please enter max password lenth 8", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
