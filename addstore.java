package com.example.onlinestore.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.R;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStoreFragment extends Fragment {
    private static final String _REGISTERID = "registerid";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    TextView storeImageTxt;
    ImageView storeImageView;
    EditText storeNamedt,addressEdt,phoneNoEdt,emailEdt,operationHourEdt;
    Button addProductBtn,cancelBtn;
    String storaName,address,phoneNo,email,operationHour,storeImage,registerId;
    GPSTracker gpsTracker;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    public AddStoreFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_store_add,null);
        storeNamedt = view.findViewById(R.id.storeNamedt);
        addressEdt = view.findViewById(R.id.addressEdt);
        phoneNoEdt = view.findViewById(R.id.phoneNoEdt);
        emailEdt = view.findViewById(R.id.emailEdt);
        operationHourEdt = view.findViewById(R.id.operationHourEdt);
        storeImageTxt = view.findViewById(R.id.storeImageTxt);
        storeImageView = view.findViewById(R.id.storeImageView);
        //shared preferences
        userPreference = new UserPreference(getActivity());
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(getActivity());
        addProductBtn = view.findViewById(R.id.addProductBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storaName = storeNamedt.getText().toString().trim();
                phoneNo = phoneNoEdt.getText().toString().trim();
                email = emailEdt.getText().toString().trim();
                address = addressEdt.getText().toString().trim();
                operationHour = operationHourEdt.getText().toString().trim();


                builder = new AlertDialog.Builder(getActivity());
                if(storaName.equals("") || email.equals("") || phoneNo.equals("") || address.equals("") || operationHour.equals("")){
                    builder.setTitle("Something went wrong.....");
                    builder.setMessage("Please fill all the fields...");
                    displayAlert("input_error");
                }
                else
                {

                    store();

                }
            }
        });

        storeImageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        return view;
    }


    public void store(){
        progressView = new ProgressDialog(getActivity());
        progressView.setMessage("Loading...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://www.islamictime.ramaulnews.com/storeAdd.php";
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
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                param.put("storaName",storaName);
                param.put("phoneNo",phoneNo);
                param.put("email",email);
                param.put("address",address);
                param.put("operationHour",operationHour);
                param.put("storeImage",ts);
                param.put("name",imageToString(bitmap));
                param.put("latitude",String.valueOf(gpsTracker.getLatitude()));
                param.put("longitude",String.valueOf(gpsTracker.getLongitude()));
                param.put("registerId",String.valueOf(userDetails.get(_REGISTERID)));

                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(requestRegister);
    }

    public void displayAlert(final String code){
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    progressView.dismiss();
                    storeNamedt.setText("");
                    phoneNoEdt.setText("");
                    emailEdt.setText("");
                    addressEdt.setText("");
                    operationHourEdt.setText("");
                }else if(code.equals("no_success")){
                    progressView.dismiss();
                    storeNamedt.setText("");
                    phoneNoEdt.setText("");
                    emailEdt.setText("");
                    addressEdt.setText("");
                    operationHourEdt.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);
                storeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }
}


