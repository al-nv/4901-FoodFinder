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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinestore.R;
import com.example.onlinestore.activity.OrderActivity;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;


import java.util.ArrayList;
import java.util.List;

public class OrderAllAdapter extends RecyclerView.Adapter<OrderAllAdapter.Poductclass> {
    OrderActivity context;
    List<CartModel> listProduct;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;

    public OrderAllAdapter(OrderActivity context, List<CartModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        userDb = new UserDb(context);
        sqLiteDatabase = userDb.getReadableDatabase();
    }

    @NonNull
    @Override
    public OrderAllAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OrderAllAdapter.Poductclass holder, int position) {

        final CartModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.quantityTxt.setText("Qty: "+info.getQuantity()+"");
        holder.rateTxt.setText("Rate: "+info.getRate()+"");
        holder.tamtTxt.setText("Total: $ "+info.getAmount()+"");
        holder.dateTxt.setText(info.getOrderDate());
        holder.storenameTxt.setText(info.getStoreName());

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView quantityTxt,rateTxt,tamtTxt,productNameTxt,dateTxt,storenameTxt;
        ImageView productImageView,locationImg;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            rateTxt = view.findViewById(R.id.rateTxt);
            tamtTxt = view.findViewById(R.id.tamtTxt);
            dateTxt = view.findViewById(R.id.dateTxt);
            storenameTxt = view.findViewById(R.id.storenameTxt);

        }
    }

}

