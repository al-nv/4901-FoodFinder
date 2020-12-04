package com.example.onlinestore.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.activity.MapsActivity;
import com.example.onlinestore.activity.ProductActivity;
import com.example.onlinestore.activity.ShowStoreLocationActivity;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.StoreModel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellerStoreAdapter extends RecyclerView.Adapter<SellerStoreAdapter.Poductclass> {
    MainActivity context;
    List<StoreModel> listProduct;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;

    public SellerStoreAdapter(MainActivity context, List<StoreModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        userDb = new UserDb(context);
        sqLiteDatabase = userDb.getReadableDatabase();
    }

    @NonNull
    @Override
    public SellerStoreAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SellerStoreAdapter.Poductclass holder, int position) {

        final StoreModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getStoraName());
        holder.phonenoTxt.setText(info.getPhoneNo());
        holder.addressTxt.setText(info.getAddress());
        holder.operationhourTxt.setText(info.getOperationHour()+" AM "+" - "+info.getCloseHour()+" PM");

        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getStoreImage() + ".jpg")
                .placeholder(R.drawable.ic_person)
                .into(holder.productImageView);


        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("storeId",info.getStoreId());
                intent.putExtra("storeName",info.getStoraName());
                context.startActivity(intent);
            }
        });

        holder.locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("storeName",info.getStoraName());
                intent.putExtra("lat",info.getLatitude());
                intent.putExtra("lng",info.getLongitude());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt,phonenoTxt,addressTxt,operationhourTxt;
        ImageView productImageView,locationImg;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            phonenoTxt = view.findViewById(R.id.phonenoTxt);
            addressTxt = view.findViewById(R.id.addressTxt);
            operationhourTxt = view.findViewById(R.id.operationhourTxt);
            productImageView = view.findViewById(R.id.productImageView);
            locationImg = view.findViewById(R.id.locationImg);
            itemLayout = view.findViewById(R.id.itemLayout);
        }
    }

    public void filterList(ArrayList<StoreModel> filterdNames) {
        this.listProduct = filterdNames;
        notifyDataSetChanged();
    }
}

