package com.example.onlinestore;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.onlinestore.activity.CartActivity;
import com.example.onlinestore.activity.FavoriteActivity;
import com.example.onlinestore.activity.OrderActivity;
import com.example.onlinestore.activity.UserRegisterActivity;
import com.example.onlinestore.adapter.SellerStoreAdapter;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.model.StoreModel;
import com.example.onlinestore.preference.UserPreference;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String _REGISTERID = "registerid";
    private static final String _USERNAME = "username";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    private AppBarConfiguration mAppBarConfiguration;
    private SwipeRefreshLayout swip_refresh_layout;
    private RecyclerView recyclerView;
    private EditText searchEdt;
    LinearLayout internet_layout;
    TextView usernameTxt;
    List<StoreModel> storeModelList;
    SellerStoreAdapter adapter;
    TextView orderNotification;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        searchEdt = findViewById(R.id.searchEdt);
        internet_layout = findViewById(R.id.internet_layout);

        swip_refresh_layout = findViewById(R.id.swip_refresh_layout);
        swip_refresh_layout.setOnRefreshListener(this);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txt_company_name = headerView.findViewById(R.id.usernameTxt);


        userDb = new UserDb(this);
        //shared preferences
        userPreference = new UserPreference(MainActivity.this);
        userDetails = userPreference.getUser();
        txt_company_name.setText(String.valueOf(userDetails.get(_USERNAME)));
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_logout,R.id.nav_profile,R.id.nav_favarite)
                .setDrawerLayout(drawer)
                .build();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        Intent intent = new Intent(MainActivity.this, UserRegisterActivity.class);
                        intent.putExtra("user","user");
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        userPreference.logoutUser();
                        new UserPreference(MainActivity.this).removeUser();
                        finish();
                        break;
                    case R.id.nav_favarite:
                        sqLiteDatabase = userDb.getReadableDatabase();
                        List<CartModel> favoriteList = userDb.retrieveFavorite(sqLiteDatabase);
                        if(favoriteList.size() > 0) {
                            Intent intentFavorite = new Intent(MainActivity.this, FavoriteActivity.class);
                            startActivity(intentFavorite);
                        }else {
                            Toast.makeText(MainActivity.this, "No favorite data", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });

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

       // if(internetIsConnected()) {
            onLoadingSwipRefresh();
//        }else {
//            internet_layout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        sqLiteDatabase = userDb.getReadableDatabase();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.badge);
        MenuItem item2 = menu.findItem(R.id.notification);
        MenuItemCompat.setActionView(item, R.layout.action_bage_layout);
        MenuItemCompat.setActionView(item2, R.layout.action_bage_notification);

        RelativeLayout notifCount = (RelativeLayout)   MenuItemCompat.getActionView(item);
        RelativeLayout notifCount2 = (RelativeLayout)   MenuItemCompat.getActionView(item2);

        List<CartModel> listCartCount = userDb.retrieveCart(sqLiteDatabase);
        TextView cartTxt = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        cartTxt.setText(String.valueOf(listCartCount.size()));
        orderNotification = (TextView) notifCount2.findViewById(R.id.actionbar_notifcation_textview);
        showallOrder();
        cartTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase = userDb.getReadableDatabase();
                List<CartModel> listIdcart = userDb.retrieveCart(sqLiteDatabase);
                if(listIdcart.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "No cart data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        orderNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
//                sqLiteDatabase = userDb.getReadableDatabase();
//                List<CartModel> favoriteList = userDb.retrieveFavorite(sqLiteDatabase);
//                if(favoriteList.size() > 0) {
//                    Intent intentFavorite = new Intent(MainActivity.this, FavoriteActivity.class);
//                    startActivity(intentFavorite);
//                }else {
//                    Toast.makeText(MainActivity.this, "No favorite data", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void showallOrder(){
        String urlFetchAllStore = "http://192.168.56.1/ShowOrderByUserId.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest requestFetchAllStore = new StringRequest(Request.Method.POST, urlFetchAllStore,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            ArrayList<Object> orderModelList = new ArrayList<>();
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
                                info.setProductName(jsonObjectData.getString("ProductName"));
                                info.setQuantity(jsonObjectData.getDouble("Quantity"));
                                info.setRate(jsonObjectData.getDouble("Rate"));
                                info.setOrderDate(jsonObjectData.getString("OrderDate"));
                                info.setAmount(jsonObjectData.getDouble("Amount"));
                                orderModelList.add(info);

                            }
                            orderNotification.setText(String.valueOf(orderModelList.size()+""));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swip_refresh_layout.setRefreshing(false);
                // progressView.dismiss();
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void showallStore(){
        String urlFetchAllStore = "http://192.168.56.1/FetchAllStore.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest requestFetchAllStore = new StringRequest(Request.Method.GET, urlFetchAllStore,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            storeModelList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                StoreModel info = new StoreModel();
                                String StoreId,StoreName,Address,PhoneNo,MobileNo,EmailId,StoreImage,Latitude,Longitude;
                                info.setStoreId(jsonObjectData.getString("StoreId"));
                                info.setStoraName(jsonObjectData.getString("StoreName"));
                                info.setAddress(jsonObjectData.getString("Address"));
                                info.setPhoneNo(jsonObjectData.getString("PhoneNo"));
                                info.setMobileNo(jsonObjectData.getString("MobileNo"));
                                info.setEmail(jsonObjectData.getString("EmailId"));
                                info.setOperationHour(jsonObjectData.getString("OpeningHour"));
                                info.setCloseHour(jsonObjectData.getString("CloseHour"));
                                info.setStoreImage(jsonObjectData.getString("StoreImage"));
                                info.setLatitude(jsonObjectData.getString("Latitude"));
                                info.setLongitude(jsonObjectData.getString("Longitude"));
                                storeModelList.add(info);

                            }
                            // progressView.dismiss();
                            swip_refresh_layout.setRefreshing(false);
                            // Toast.makeText(getActivity(), ""+String.valueOf(countmember), Toast.LENGTH_SHORT).show();
                            if(storeModelList.size() > 0){
                                recyclerView.setAdapter(new SellerStoreAdapter(MainActivity.this,storeModelList));
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
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestFetchAllStore.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(requestFetchAllStore);
    }

    private void onLoadingSwipRefresh(){
        swip_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                showallStore();
            }
        });

    }

    @Override
    public void onRefresh() {

        if(internetIsConnected()) {
            showallStore();
        }else {
            internet_layout.setVisibility(View.VISIBLE);
        }

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<StoreModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (StoreModel list : storeModelList) {
            //if the existing elements contains the search input
            if (list.getStoraName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(list);
                adapter = new SellerStoreAdapter(MainActivity.this,filterdNames);
                recyclerView.setAdapter(adapter);
            }

        }


        //calling a method of the adapter class and passing the filtered list
        adapter = new SellerStoreAdapter(MainActivity.this,filterdNames);
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
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Do you want to Exit from the App?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }


//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}
