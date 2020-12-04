package com.example.onlinestore.adapter;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinestore.R;
import com.example.onlinestore.activity.FavoriteActivity;
import com.example.onlinestore.global.Constants;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.Poductclass> {
    FavoriteActivity context;
    ProgressDialog progressView;
    List<CartModel> listProduct;
    private static final String _REGISTERID = "registerid";
    AlertDialog.Builder builder;
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    GPSTracker gpsTracker;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    int qty;

    public FavoriteListAdapter(FavoriteActivity context, List<CartModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        //shared preferences
        userPreference = new UserPreference(context);
        userDetails = userPreference.getUser();
        builder = new AlertDialog.Builder(context);
        userDb  = new UserDb(context);
        sqLiteDatabase = userDb.getReadableDatabase();
        gpsTracker = new GPSTracker(context);
    }

    @NonNull
    @Override
    public FavoriteListAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull FavoriteListAdapter.Poductclass holder, final int position) {

        final CartModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.priceTxt.setText("Rs "+String.format("%.2f",info.getRate()));
        holder.quantityTxt.setText("Qty "+info.getQuantity());
        qty = (int)info.getQuantity();
        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getProductImage() + ".jpg")
                .placeholder(R.drawable.ic_person)
                .into(holder.productImageView);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Server Response....");
                builder.setMessage("Are you sure want to delete item");
                displayAlertDelete(String.valueOf(info.getId()));
            }
        });
        //  Toast.makeText(context, ""+info.getStoreId(), Toast.LENGTH_SHORT).show();
        holder.addtocartTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase = userDb.getReadableDatabase();
                List<CartModel> listIdcart = userDb.retrieveCartById(info.getProductId(), sqLiteDatabase);
                if(listIdcart.size() > 0){
                    Toast.makeText(context, "Already exits this item in a cart", Toast.LENGTH_SHORT).show();
                }else {
                    builder.setTitle("Server Response....");
                    builder.setMessage("Are you sure want to add to  cart");
                    // userDb.insertFavorite(info.getProductId(), Constants.storeId,String.valueOf(userDetails.get(_REGISTERID)),info.getProductName(),info.getQuantity(),info.getPrice(),tAmt, String.valueOf(formatter.format(date)), String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()), info.getProductImage(), sqLiteDatabase);
                    displayAlertAddCart(info.getStoreId(),String.valueOf(info.getId()),info.getProductId(),info.getStoreId(), info.getProductName(), String.valueOf(info.getStockInQuantity()),String.valueOf(info.getQuantity()), String.valueOf(info.getRate()), info.getProductImage());
                    // userDb.insertCart(info.getProductId(), info.getProductName(), String.valueOf(info.getQty()), String.valueOf(info.getPrice()), String.valueOf(formatter.format(date)), String.valueOf(tsLong), info.getUrlImage(), sqLiteDatabase);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt,quantityTxt,priceTxt,addtocartTxt;
        ImageView productImageView,deleteImg;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            addtocartTxt = view.findViewById(R.id.aaddtocartTxt);
            productImageView = view.findViewById(R.id.productImageView);
            deleteImg = view.findViewById(R.id.deleteImg);
            itemLayout = view.findViewById(R.id.itemLayout);
        }
    }

    public void displayAlertAddCart(final String storeId, final String id, final String productId,final String StoreName, final String productName,final String StockQuantity, final String qty, final String price, final String productImage) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Long tsLong = System.currentTimeMillis() / 1000;
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                double tAmt = Double.parseDouble(qty) * Double.parseDouble(price);

                userDb.insertCart(productId, storeId,String.valueOf(userDetails.get(_REGISTERID)),StoreName,productName,StockQuantity,qty,price,tAmt, String.valueOf(formatter.format(date)), String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()), productImage, sqLiteDatabase);

                userDb.deleteFavorite(id, sqLiteDatabase);
                context.favoriteShow();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressView.dismiss();


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void displayAlertDelete(final String productId) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDb.deleteFavorite(productId,sqLiteDatabase);
                context.favoriteShow();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressView.dismiss();


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
