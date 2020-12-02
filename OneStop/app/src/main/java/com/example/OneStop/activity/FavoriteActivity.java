package com.example.onlinestore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;


import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.adapter.CartListAdapter;
import com.example.onlinestore.adapter.FavoriteListAdapter;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    RecyclerView recyclerViewCart;
    TextView totalAmttxt;
    CartListAdapter cartListAdapter;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    List<CartModel> cartList;
    double totalAmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Favorite");

        totalAmttxt = findViewById(R.id.totalAmttxt);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        userDb = new UserDb(this);
        favoriteShow();
    }

    public void favoriteShow(){
        sqLiteDatabase = userDb.getReadableDatabase();
        cartList = userDb.retrieveFavorite(sqLiteDatabase);
        recyclerViewCart.setAdapter(new FavoriteListAdapter(this,cartList));

        for (CartModel info : cartList){
            totalAmt +=(info.getQuantity() * info.getRate());
        }
        if(cartList.size() > 0){

        }else {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        totalAmttxt.setText("Total:- "+String.format("%.2f",totalAmt));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return super.onSupportNavigateUp();
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//        super.onBackPressed();
//    }
}
