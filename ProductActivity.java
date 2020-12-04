package com.example.onlinestore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.R;
import com.example.onlinestore.adapter.UserProductAdapter;
import com.example.onlinestore.global.Constants;
import com.example.onlinestore.model.ProductModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swip_refresh_layout;
    private RecyclerView recyclerView;
    private EditText searchEdt;
    List<ProductModel> productModelList;
    UserProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("storeName"));

        getSupportActionBar().setElevation(0);
        searchEdt = findViewById(R.id.searchEdt);
        swip_refresh_layout = findViewById(R.id.swip_refresh_layout);
        swip_refresh_layout.setOnRefreshListener(this);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Constants.storeId = getIntent().getStringExtra("storeId");
        Constants.storeName = getIntent().getStringExtra("storeName");
      //  if(internetIsConnected()) {
            onLoadingSwipRefresh();
//        }else {
//            Toast.makeText(this, "please connect internet", Toast.LENGTH_SHORT).show();
//        }

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //after the change calling the method and passing the search input
                filter(s.toString().toLowerCase());

            }
        });
    }

    public void showallProduct(){
        String url = "http://192.168.56.1/FetchProductStock.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            productModelList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                ProductModel info = new ProductModel();
                                info.setProductId(jsonObjectData.getString("ProductId"));
                                info.setProductName(jsonObjectData.getString("ProductName"));
                                info.setColor(jsonObjectData.getString("Color"));
                                info.setQuantity(jsonObjectData.getString("StockInQuantity"));
                                info.setManufactureDate(jsonObjectData.getString("ManufactureDate"));
                                info.setExpiryDate(jsonObjectData.getString("ExpiryDate"));
                                info.setProductImage(jsonObjectData.getString("ProductImage1"));
                                info.setPrice(jsonObjectData.getString("SalesRate"));
                                productModelList.add(info);
                            }
                            swip_refresh_layout.setRefreshing(false);
                            if(productModelList.size() > 0){
                                recyclerView.setAdapter(new UserProductAdapter(ProductActivity.this,productModelList));
                            }else {
                                Toast.makeText(ProductActivity.this, "No data available this store", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ProductActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("StoreId",getIntent().getStringExtra("storeId"));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void onLoadingSwipRefresh(){
        swip_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                showallProduct();
            }
        });

    }
    @Override
    public void onRefresh() {
        showallProduct();
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ProductModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ProductModel list : productModelList) {
            //if the existing elements contains the search input
            if (list.getProductName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(list);
                adapter = new UserProductAdapter(ProductActivity.this,filterdNames);
                recyclerView.setAdapter(adapter);
            }

        }


        //calling a method of the adapter class and passing the filtered list
        adapter = new UserProductAdapter(ProductActivity.this,filterdNames);
        adapter.filterList(filterdNames);
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
