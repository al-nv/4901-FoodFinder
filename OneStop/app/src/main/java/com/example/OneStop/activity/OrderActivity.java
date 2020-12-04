package com.example.onlinestore.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
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
import com.example.onlinestore.adapter.OrderAllAdapter;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swip_refresh_layout;
    private RecyclerView recyclerView;
    private static final String _REGISTERID = "registerid";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;

    List<CartModel> orderModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("your Orders");

        swip_refresh_layout = findViewById(R.id.swip_refresh_layout);
        swip_refresh_layout.setOnRefreshListener(this);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        onLoadingSwipRefresh();
    }

    @Override
    public void onRefresh() {
        showallOrder();
    }

    public void showallOrder(){
        String urlFetchAllStore = "http://192.168.56.1/ShowOrderByUserId.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest requestFetchAllStore = new StringRequest(Request.Method.POST, urlFetchAllStore,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            orderModelList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                CartModel info = new CartModel();
                                String OrderId,ProductId,StoreId,UserId,Quantity,Rate,OrderDate,Amount;
                                info.setStoreId(jsonObjectData.getString("OrderId"));
                                info.setProductId(jsonObjectData.getString("ProductId"));
                                info.setStoreId(jsonObjectData.getString("StoreId"));
                                info.setUserId(jsonObjectData.getString("UserId"));
                                info.setStoreName(jsonObjectData.getString("StoreName"));
                                info.setProductName(jsonObjectData.getString("ProductName"));
                                info.setQuantity(jsonObjectData.getDouble("Quantity"));
                                info.setRate(jsonObjectData.getDouble("Rate"));
                                info.setOrderDate(jsonObjectData.getString("OrderDate"));
                                info.setAmount(jsonObjectData.getDouble("Amount"));

                                orderModelList.add(info);

                            }
                            // progressView.dismiss();
                            swip_refresh_layout.setRefreshing(false);
                            // Toast.makeText(getActivity(), ""+String.valueOf(countmember), Toast.LENGTH_SHORT).show();
                            if(orderModelList.size() > 0){
                                recyclerView.setAdapter(new OrderAllAdapter(OrderActivity.this,orderModelList));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swip_refresh_layout.setRefreshing(false);
                // progressView.dismiss();
                Toast.makeText(OrderActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                param.put("UserId", String.valueOf(userDetails.get(_REGISTERID)));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestFetchAllStore.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestFetchAllStore);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void onLoadingSwipRefresh(){
        swip_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                showallOrder();
            }
        });

    }
}

