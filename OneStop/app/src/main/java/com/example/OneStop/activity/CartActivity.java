package com.example.onlinestore.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.adapter.CartListAdapter;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.gps.Globle;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    private static final String _REGISTERID = "registerid";
    private static final String _STOREID = "storeId";
    RecyclerView recyclerViewCart;
    TextView totalAmttxt, cnfTxt;
    CartListAdapter cartListAdapter;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    List<CartModel> cartList;
    double totalAmt;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    Object orderobject;
    Globle internetCheck;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cart");
        builder = new AlertDialog.Builder(CartActivity.this);
        cnfTxt = findViewById(R.id.cnfTxt);
        totalAmttxt = findViewById(R.id.totalAmttxt);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        userDb = new UserDb(this);
        sqLiteDatabase = userDb.getReadableDatabase();
        //shared preferences
        internetCheck = new Globle(this);
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(this);
        cnfTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Server response.....");
                builder.setMessage("Are you Sure want to confirm order...");
                if(internetCheck.getConnectivityStatus(getApplicationContext())) {
                    displayAlert("reg_success");
                }else {
                    Toast.makeText(CartActivity.this, "Please connect internet....", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cartShow();
    }

    public void cartShow() {
        totalAmt=0;
        cartList = userDb.retrieveCart(sqLiteDatabase);
        recyclerViewCart.setAdapter(new CartListAdapter(this, cartList));

        for (CartModel info : cartList) {
            totalAmt += (info.getQuantity() * info.getRate());
        }
        if(cartList.size() > 0){

        }else {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        totalAmttxt.setText("Total:- $ " + String.format("%.2f", totalAmt));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return super.onSupportNavigateUp();
    }


    public void order() {
        progressView = new ProgressDialog(this);
        progressView.setMessage("Register ...");
        progressView.setCancelable(false);
        progressView.show();
        String urlSalesOrder = "http://192.168.56.1/SalesOrder.php";
        StringRequest requestSalesorder = new StringRequest(Request.Method.POST, urlSalesOrder,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // try {
                            userDb.deleteCartAll(sqLiteDatabase);
                         showNotificationSpecificUser();

//                            JSONArray jsonArray = new JSONArray(response);
//                            JSONObject jsonObject = jsonArray.getJSONObject(0);
//                            String code = jsonObject.getString("code");
//                            String message = jsonObject.getString("message");
//                            if (code.equals("reg_success")) {
//                                Toast.makeText(CartActivity.this, "yes" + message, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(CartActivity.this, "no", Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.dismiss();
                Toast.makeText(CartActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                ArrayList<HashMap> models = new ArrayList<HashMap>();
                SQLiteDatabase db = userDb.getReadableDatabase();

                List<CartModel> listOrder = userDb.retrieveCart(db);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                JSONObject obj = null;
                JSONArray orderedPayload = new JSONArray();
                for (CartModel data : listOrder) {
                    obj = new JSONObject();
                    //ProductId,StoreId,UserId,Quantity,Rate,Amount,OrderDate,Latitude,Longitude,IsOrderCancel,IsOrderDelivery
                    hashMap.put("ProductId", data.getProductId());
                    hashMap.put("StoreId", String.valueOf(data.getStoreId()));
                    hashMap.put("UserId", String.valueOf(userDetails.get(_REGISTERID)));
                    hashMap.put("StoreName", String.valueOf(data.getStoreName()));
                    hashMap.put("ProductName", String.valueOf(data.getProductName()));
                    hashMap.put("Quantity", String.valueOf(data.getQuantity()));

                    double stockQuanty = data.getStockInQuantity() - data.getQuantity();
                    hashMap.put("StockBalanceQuantity", String.valueOf(stockQuanty));

                    hashMap.put("Rate", String.valueOf(data.getRate()));
                    hashMap.put("Amount", String.valueOf(data.getAmount()));
                    hashMap.put("OrderDate", String.valueOf(data.getOrderDate()));
                    hashMap.put("Latitude",  String.valueOf(gpsTracker.getLatitude()));
                    hashMap.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                    hashMap.put("IsOrderCancel", "1");
                    hashMap.put("IsOrderDelivery", "1");
                    orderedPayload.put(new JSONObject(hashMap));
                }
                param.put("json", orderedPayload.toString());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestSalesorder.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestSalesorder);
    }

    public void displayAlert(final String code){
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    order();
                    Intent intent = new Intent(CartActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(code.equals("no_success")){
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showNotificationSpecificUser()
    {
        OneSignal.sendTag("Order User_ID",userDetails.get(_REGISTERID));
        OSPermissionSubscriptionState states = OneSignal.getPermissionSubscriptionState();
        String UserId = states.getSubscriptionStatus().getUserId();
        boolean isSubcribed = states.getSubscriptionStatus().getSubscribed();
        if(!isSubcribed)
            return;
        try {
            JSONObject notificationContent = new JSONObject(
                    "{'contents': {'en': 'Your Order Confirm'}," +
                            "'include_player_ids': ['"+UserId+"'], " +
                            "'headings': {'en': 'Confirm Oder'}," +
                            "'app_id': \"56e53808-fe83-4174-8aef-ac507a7e5b75\""+
                            "}");
            OneSignal.postNotification(notificationContent,null);
        }catch (JSONException e){

        }


    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

}
