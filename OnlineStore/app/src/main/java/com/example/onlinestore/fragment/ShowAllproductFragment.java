package com.example.onlinestore.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.onlinestore.adapter.SellerProductAdapter;
import com.example.onlinestore.model.ProductModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowAllproductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    SellerProductAdapter adapter;
    private SwipeRefreshLayout swip_refresh_layout;
    private static final String _REGISTERID = "registerid";
    private static final String _STOREID = "storeId";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    String productName,price,color,quantity,exp_date,productImage,status;
    List<ProductModel> productModelList;
    public ShowAllproductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //shared preferences
        userPreference = new UserPreference(getActivity());
        userDetails = userPreference.getUser();
        View view = inflater.inflate(R.layout.fragment_show_allproduct, null);
        recyclerView = view.findViewById(R.id.recyclerView);
        swip_refresh_layout = view.findViewById(R.id.swip_refresh_layout);
        swip_refresh_layout.setOnRefreshListener(this);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        onLoadingSwipRefresh();
        return view;
    }

    public void showallData(){
        String urlFetchProduct = "http://192.168.56.1/FetchProductStock.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest requestFetchProduct = new StringRequest(Request.Method.POST, urlFetchProduct,
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
                                //  ProductName,Color,ProductImage1,StockInQuantity,SalesRate,ManufactureDate,ExpiryDate
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
                            // progressView.dismiss();
                            swip_refresh_layout.setRefreshing(false);
                            // Toast.makeText(getActivity(), ""+String.valueOf(countmember), Toast.LENGTH_SHORT).show();
                            if(productModelList.size() > 0){
                                adapter = new SellerProductAdapter(getActivity(),productModelList);
                                recyclerView.setAdapter(adapter);
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
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("StoreId",String.valueOf(userDetails.get(_STOREID)));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestFetchProduct.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestFetchProduct);
    }

    private void onLoadingSwipRefresh(){
        swip_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                showallData();
            }
        });

    }

    @Override
    public void onRefresh() {

        showallData();
    }
}
