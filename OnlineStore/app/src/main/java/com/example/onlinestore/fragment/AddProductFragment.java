package com.example.onlinestore.fragment;


import android.app.DatePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.onlinestore.R;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private String datePickDay, catagory, code;
    private static final String _REGISTERID = "registerid";
    private static final String _STOREID = "storeId";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    TextView productImageTxt;
    long unixTime;
    String ProductId;
    ImageView productImageView;
    EditText productNameEdt,priceEdt,colorEdt,quantityEdt,exrDateEdt,manuDateEdt;
    Button addProductBtn,cancelBtn;
    String productName,price,color,quantity,exp_date,manufaData,productImage,registerId;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    String storeId=null,storaName=null,	latitude=null,longitude=null;
    public AddProductFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_item,null);
        productNameEdt = view.findViewById(R.id.productNameEdt);
        priceEdt = view.findViewById(R.id.priceEdt);
        productImageTxt = view.findViewById(R.id.productImageTxt);
        colorEdt = view.findViewById(R.id.colorEdt);
        quantityEdt = view.findViewById(R.id.quantityEdt);
        exrDateEdt = view.findViewById(R.id.exrDateEdt);
        manuDateEdt = view.findViewById(R.id.manuDateEdt);
        productImageView = view.findViewById(R.id.productImageView1);
        addProductBtn = view.findViewById(R.id.addProductBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        //shared preferences
        userPreference = new UserPreference(getActivity());
        userDetails = userPreference.getUser();

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = productNameEdt.getText().toString().trim();
                price = priceEdt.getText().toString().trim();
                color = colorEdt.getText().toString().trim();
                quantity = quantityEdt.getText().toString().trim();
                exp_date = exrDateEdt.getText().toString().trim();
                manufaData = manuDateEdt.getText().toString().trim();


                builder = new AlertDialog.Builder(getActivity());
                if(productName.equals("") || price.equals("") || color.equals("") || quantity.equals("") || exp_date.equals("")){
                    builder.setTitle("Something went wrong.....");
                    builder.setMessage("Please fill all the fields...");
                    displayAlert("input_error");
                }
                else
                {

                    productMaster();

                }
            }
        });

//        exrDateEdt.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    //  if (event.getRawX() >= (edt_date_picker.getRight() - edt_date_picker.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                    // your action here
//                    // Toast.makeText(ReceiveAndPayment.this,"click",Toast.LENGTH_SHORT).show();
//                    final Calendar c = Calendar.getInstance();
//
//
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH);
//                    day = c.get(Calendar.DAY_OF_MONTH);
//
//                    //date picker dialog
//                    datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
//                            year = selectedYear;
//                            month = selectedMonth + 1;
//                            day = selectedDay;
//                            String strYear = String.valueOf(year);
//                            String strmonth = String.valueOf(month);
//                            // String strday = String.valueOf(day);
//                            SimpleDateFormat monthParse = new SimpleDateFormat("M");
//                            SimpleDateFormat monthParseN = new SimpleDateFormat("MM");
//                            SimpleDateFormat monthDisplay = new SimpleDateFormat("MMM");
//
//
//                            DateFormat dt = new SimpleDateFormat("yyy-MMM-dd");
//                            String date = dt.format(c.getTime());
//
//                            // datePickDay = strYear + "" + strmonth + "" + day;
//                            try {
//                                //datePickDay = strYear + "-" + strmonth + "-" + day;
//                                datePickDay = strYear + "-" + monthDisplay.format(monthParseN.parse(strmonth)) + "-" + day;
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            try {
//                                unixTime = dt.parse(datePickDay).getTime() / 1000L;
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            exrDateEdt.setText(datePickDay);
//                            // Toast.makeText(PdcActivity.this,String.valueOf(unixTime)+ "", Toast.LENGTH_SHORT).show();
//                        }
//                    }, year, month, day);
//                    // disable max date
//                    // datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
//                    datePickerDialog.show();
//
//                    return true;
//
//                }
//                return false;
//            }
//        });

        productImageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });

        return view;
    }
    public void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    public void productMaster(){

        progressView = new ProgressDialog(getActivity());
        progressView.setMessage("loading Product...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://192.168.56.1/AddProductMaster.php";
        StringRequest requestAddProductMaster = new StringRequest(Request.Method.POST,urlRegister,
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
                // ProductName,Color,ProductImage1,ProductImage2,ProductImage3,Status

                param.put("ProductName",productName);
                param.put("Color",color);
                param.put("ProductImage1",ts);
                param.put("ProductImage2",ts);
                param.put("ProductImage3",ts);
                param.put("Status","1");
                param.put("name1",imageToString(bitmap));
                param.put("name2",imageToString(bitmap));
                param.put("name3",imageToString(bitmap));

                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestAddProductMaster.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestAddProductMaster);


    }
    public void productStock(){

        String urladdProductStock = "http://192.168.56.1/AddProductStock.php";
        StringRequest requestAddProduct = new StringRequest(Request.Method.POST,urladdProductStock,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
//                            builder.setTitle("Server Response....");
//                            builder.setMessage(message);
//                            displayAlert(code);
                            // profileImage.setImageResource(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("ProductId",ProductId);
                param.put("StoreId",String.valueOf(userDetails.get(_STOREID)));
                param.put("ManufactureDate",manufaData);
                param.put("ExpiryDate",exp_date);
                param.put("StockInQuantity",quantity);
                param.put("SalesRate",price);
                return param;
            }
        };


        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestAddProduct.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestAddProduct);
    }

    public void displayAlert(final String code){
        productfetchMaxId();
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    progressView.dismiss();
                    productNameEdt.setText("");
                    priceEdt.setText("");
                    colorEdt.setText("");
                    quantityEdt.setText("");
                    exrDateEdt.setText("");
                    manuDateEdt.setText("");

                }else if(code.equals("no_success")){
                    progressView.dismiss();
                    productNameEdt.setText("");
                    priceEdt.setText("");
                    colorEdt.setText("");
                    quantityEdt.setText("");
                    exrDateEdt.setText("");
                    manuDateEdt.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);
                productImageView.setImageBitmap(bitmap);
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


    public void productfetchMaxId(){
        String urlStore = "http://192.168.56.1/maxIdfromProductMaster.php";
        StringRequest requestMaxId = new StringRequest(Request.Method.POST,urlStore,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if(code.equals("ProductId_success")) {
                                ProductId = jsonObject.getString("ProductId");
                                productStock();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestMaxId.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestMaxId);
    }
}
