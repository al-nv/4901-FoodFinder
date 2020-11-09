package com.example.onlinestore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.adapter.CartListAdapter;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    private static final String _REGISTERID = "registerid";
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
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(this);
        cnfTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Server response.....");
                builder.setMessage("Are Sure want to confirm order...");
                displayAlert("reg_success");
            }
        });

        cartShow();
    }

    public void cartShow() {
        cartList = userDb.retrieveCart(sqLiteDatabase);
        recyclerViewCart.setAdapter(new CartListAdapter(this, cartList));

        for (CartModel info : cartList) {
            totalAmt += (info.getQty() * info.getPrice());
        }
        totalAmttxt.setText("Total:- " + String.format("%.2f", totalAmt));
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
        String urlRegister = "http://192.168.56.1/orderProduct.php";
        StringRequest requestRegister = new StringRequest(Request.Method.POST, urlRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            userDb.deleteCartAll(sqLiteDatabase);
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            if (code.equals("reg_success")) {
                                Toast.makeText(CartActivity.this, "yes" + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CartActivity.this, "no", Toast.LENGTH_SHORT).show();

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
                    hashMap.put("productId", data.getProductId());
                    hashMap.put("productName", String.valueOf(data.getProductName()));
                    hashMap.put("qty", String.valueOf(data.getQty()));
                    hashMap.put("price", String.valueOf(data.getPrice()));
                    hashMap.put("orderDate", String.valueOf(data.getOrderDate()));
                    hashMap.put("orderNo", String.valueOf(data.getOrderNo()));
                    hashMap.put("latitude", String.valueOf(gpsTracker.getLatitude()));
                    hashMap.put("longitude", String.valueOf(gpsTracker.getLongitude()));
                    hashMap.put("registerId", String.valueOf(userDetails.get(_REGISTERID)));
                    orderedPayload.put(new JSONObject(hashMap));
                }
                param.put("json", orderedPayload.toString());
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(requestRegister);
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
}