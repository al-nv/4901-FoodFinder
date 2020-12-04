package com.example.onlinestore.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.global.Constants;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddtoCartactivity extends AppCompatActivity {
    private static final String _REGISTERID = "registerid";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    Button addcartBtn;
    ImageView productImageView,minusImageView,addImageView;
    TextView productNameTxt,productPriceTxt,qtyTxt;
    String productId,productName,productQty,productPrice,productImage;
    int count=1;
    double totalAmt;
    String urlImage;
    private Bitmap bitmap;
    GPSTracker gpsTracker;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_cartactivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("productName"));
        productImageView = findViewById(R.id.productImageView);
        productNameTxt = findViewById(R.id.productNameTxt);
        productPriceTxt = findViewById(R.id.productPriceTxt);
        qtyTxt = findViewById(R.id.qtyTxt);
        addcartBtn = findViewById(R.id.addcartBtn);
        addImageView = findViewById(R.id.addImageView);
        minusImageView = findViewById(R.id.minusImageView);
        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(this);
        userDb = new UserDb(this);
        sqLiteDatabase = userDb.getReadableDatabase();

        productNameTxt.setText(getIntent().getStringExtra("productName"));
        productPriceTxt.setText(getIntent().getStringExtra("productPrice"));
        //  qtyTxt.setText(getIntent().getStringExtra("StockInQuantity"));

        Glide.with(AddtoCartactivity.this)
                .load("http://192.168.56.1/storeImage/"+getIntent().getStringExtra("productImage")+".jpg")
                .placeholder(R.drawable.ic_broken)
                .into(productImageView);

        urlImage = "http://192.168.56.1/storeImage/"+getIntent().getStringExtra("productImage")+".jpg";
        qtyTxt.setText(String.valueOf(count));
        //  count = Integer.parseInt(getIntent().getStringExtra("productQty"));
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAmt=0;
                count++;
                totalAmt = Double.parseDouble(getIntent().getStringExtra("productPrice")) * count;
                qtyTxt.setText(String.valueOf(count));
                // productPriceTxt.setText(String.valueOf("Rs :"+totalAmt));
                productPriceTxt.setText(String.format("%.2f",totalAmt));
            }
        });
        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAmt=0;
                if(count > 1){
                    count--;

                }
                totalAmt = Double.parseDouble(getIntent().getStringExtra("productPrice")) * count;
                qtyTxt.setText(String.valueOf(count));
                //  productPriceTxt.setText("Rs :"+String.valueOf(totalAmt));
                productPriceTxt.setText(String.format("%.2f",totalAmt));
            }
        });



    }



    public void addToBtn(View view) {
        productName = productNameTxt.getText().toString().trim();
        productPrice = productPriceTxt.getText().toString().trim();
        productQty = qtyTxt.getText().toString().trim();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Server Response....");
        builder.setMessage("Are you sure add to cart...");
        String code = "reg_success";
        sqLiteDatabase = userDb.getReadableDatabase();
        List<CartModel> listIdcart = userDb.retrieveCartByStoreId(Constants.storeId,sqLiteDatabase);
        if(listIdcart.size() > 0){
            displayAlert(code);
        }else {

            if(listIdcart.size() <= 0){
                displayAlert(code);
            }else{
                Toast.makeText(AddtoCartactivity.this, "Please before checkout then add to cart.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void displayAlert(final String code){
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    //  progressView.dismiss();
                    Long tsLong = System.currentTimeMillis()/1000;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
                    Date date = new Date();
                    sqLiteDatabase = userDb.getReadableDatabase();
                    List<CartModel> listIdcart = userDb.retrieveCartById(String.valueOf(getIntent().getStringExtra("productId")), sqLiteDatabase);
                    if(listIdcart.size() > 0){
                        Toast.makeText(AddtoCartactivity.this, "Already exits this item in a cart", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(AddtoCartactivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        sqLiteDatabase = userDb.getWritableDatabase();
                        // ProductId,StoreId,UserId,Quantity,Rate,Amount,OrderDate,Latitude,Longitude
                       // Toast.makeText(AddtoCartactivity.this, ""+, Toast.LENGTH_SHORT).show();
                        double tAmt = Double.parseDouble(productQty) * Double.parseDouble(productPrice);
                        Toast.makeText(AddtoCartactivity.this, ""+Constants.storeName, Toast.LENGTH_SHORT).show();
                        userDb.insertCart(getIntent().getStringExtra("productId"), Constants.storeId,Constants.storeName,String.valueOf(userDetails.get(_REGISTERID)),getIntent().getStringExtra("productName"),getIntent().getStringExtra("StockInQuantity"), productQty, getIntent().getStringExtra("productPrice"),tAmt, String.valueOf(formatter.format(date)), String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()), getIntent().getStringExtra("productImage"), sqLiteDatabase);
                    }
                }else if(code.equals("no_success")){
                    // progressView.dismiss();

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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void preparedNotificationMessage(String orderId){
        //when user place order, send noti to seller

        //prepared data for notification
       // String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order" + orderId;
        String NOTIFICATION_MESSAGE = "Congratulation...! You have new order";
        String NOTIFICATION_TYPE = "NewOrder";

        //prepare json what to send and where to send

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {

            // what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",String.valueOf(userDetails.get(_REGISTERID)));
            notificationBodyJo.put("sellerUid","");
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
           // notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){
            Toast.makeText(AddtoCartactivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo,orderId);

    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // after sending fcm star order activity

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // if fail after sending fcm star order activity
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
               // headers.put("Authorization","key"+Constants.FCK_KEY);
                return headers;
            }
        };
        Volley.newRequestQueue(AddtoCartactivity.this).add(jsonObjectRequest);

    }
}
