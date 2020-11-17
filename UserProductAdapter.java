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
import com.example.onlinestore.activity.AddtoCartactivity;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.model.ProductModel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.Poductclass> {
    MainActivity context;
    List<ProductModel> listProduct;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;

    public UserProductAdapter(MainActivity context, List<ProductModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        userDb = new UserDb(context);
    }

    @NonNull
    @Override
    public UserProductAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UserProductAdapter.Poductclass holder, int position) {

        final ProductModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.priceTxt.setText("$ "+info.getPrice());
        holder.colorTxt.setText(info.getColor());
        holder.quantityTxt.setText("Qty "+info.getQuantity());

        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getProductImage() + ".jpg")
                .placeholder(R.drawable.ic_person)
                .into(holder.productImageView);

        if (info.getStatus().equals("on")) {
            holder.avalableTxt.setText("Available");
            holder.avalableTxt.setBackgroundColor(R.color.green);
        } else {
            holder.avalableTxt.setText("N/A");
            holder.avalableTxt.setBackgroundColor(R.color.black);
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddtoCartactivity.class);
                intent.putExtra("productName",info.getProductName());
                intent.putExtra("productId",info.getProductId());
                intent.putExtra("productQty",info.getQuantity());
                intent.putExtra("productPrice",info.getPrice());
                intent.putExtra("productImage",info.getProductImage());
                context.startActivity(intent);
            }
        });
        holder.favariteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis()/1000;
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                sqLiteDatabase = userDb.getReadableDatabase();
                List<CartModel> listIdFavorite = userDb.retrieveFavoriteById(info.getProductId(), sqLiteDatabase);
                if(listIdFavorite.size() > 0){
                    Toast.makeText(context, "Already exits this item in a Favorite", Toast.LENGTH_SHORT).show();
                }else {
                    userDb.insertFavorite(info.getProductId(),info.getProductName(),"1",info.getPrice(),String.valueOf(formatter.format(date)),String.valueOf(tsLong),info.getProductImage(),sqLiteDatabase);
                    Toast.makeText(context, "successfully add to favorite", Toast.LENGTH_LONG).show();
                    context.showfavarite();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt,colorTxt,quantityTxt,priceTxt,avalableTxt;
        ImageView productImageView,favariteImg;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            colorTxt = view.findViewById(R.id.colorTxt);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            avalableTxt = view.findViewById(R.id.avalableTxt);
            productImageView = view.findViewById(R.id.productImageView);
            favariteImg = view.findViewById(R.id.favariteImg);
            itemLayout = view.findViewById(R.id.itemLayout);
        }
    }
    public void filterList(ArrayList<ProductModel> filterdNames) {
        this.listProduct = filterdNames;
        notifyDataSetChanged();
    }
}

