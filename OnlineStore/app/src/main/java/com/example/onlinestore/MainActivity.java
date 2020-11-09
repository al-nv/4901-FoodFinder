package com.example.onlinestore;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onlinestore.activity.CartActivity;
import com.example.onlinestore.activity.FavoriteActivity;
import com.example.onlinestore.activity.RegisterActivity;
import com.example.onlinestore.adapter.UserProductAdapter;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.model.ProductModel;
import com.example.onlinestore.preference.UserPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
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
    TextView usernameTxt;
    List<ProductModel> productModelList;
    UserProductAdapter adapter;
    TextView favoraite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txt_company_name = headerView.findViewById(R.id.usernameTxt);
        searchEdt = findViewById(R.id.searchEdt);
       // usernameTxt = findViewById(R.id.usernameTxt);
        swip_refresh_layout = findViewById(R.id.swip_refresh_layout);
        swip_refresh_layout.setOnRefreshListener(this);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userDb = new UserDb(this);
        //shared preferences
        userPreference = new UserPreference(MainActivity.this);
        userDetails = userPreference.getUser();
        txt_company_name.setText(String.valueOf(userDetails.get(_USERNAME)));
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_logout,R.id.nav_favarite)
                .setDrawerLayout(drawer)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
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

        onLoadingSwipRefresh();
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
        favoraite = (TextView) notifCount2.findViewById(R.id.actionbar_notifcation_textview);
        showfavarite();
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
        favoraite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase = userDb.getReadableDatabase();
                List<CartModel> favoriteList = userDb.retrieveFavorite(sqLiteDatabase);
                if(favoriteList.size() > 0) {
                    Intent intentFavorite = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intentFavorite);
                }else {
                    Toast.makeText(MainActivity.this, "No favorite data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void showallData(){
//        final ProgressDialog progressView = new ProgressDialog(getActivity());
//        progressView.setMessage("Loading data...");
//        progressView.setCancelable(true);
//        progressView.show();
        String url = "http://192.168.56.1/fetchProduct.php";
        swip_refresh_layout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, url,
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
                                info.setProductId(jsonObjectData.getString("productId"));
                                info.setProductName(jsonObjectData.getString("productName"));
                                info.setPrice(jsonObjectData.getString("price"));
                                info.setColor(jsonObjectData.getString("color"));
                                info.setQuantity(jsonObjectData.getString("quantity"));
                                info.setExp_date(jsonObjectData.getString("exp_date"));
                                info.setProductImage(jsonObjectData.getString("productImage"));
                                info.setStatus(jsonObjectData.getString("status"));
                                productModelList.add(info);

                            }
                            // progressView.dismiss();
                            swip_refresh_layout.setRefreshing(false);
                            // Toast.makeText(getActivity(), ""+String.valueOf(countmember), Toast.LENGTH_SHORT).show();
                            if(productModelList.size() > 0){
                                recyclerView.setAdapter(new UserProductAdapter(MainActivity.this,productModelList));
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void onLoadingSwipRefresh(){
        swip_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                showallData();
            }
        });

    }
    public void showfavarite(){
        List<CartModel> listfavariteCount = userDb.retrieveFavorite(sqLiteDatabase);
        favoraite.setText(String.valueOf(listfavariteCount.size()));
    }

    @Override
    public void onRefresh() {

        showallData();
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
                adapter = new UserProductAdapter(MainActivity.this,filterdNames);
                recyclerView.setAdapter(adapter);
            }

        }


        //calling a method of the adapter class and passing the filtered list
        adapter = new UserProductAdapter(MainActivity.this,filterdNames);
        adapter.filterList(filterdNames);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}