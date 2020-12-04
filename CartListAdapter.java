package com.example.onlinestore.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.onlinestore.R;
import com.example.onlinestore.activity.CartActivity;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.Poductclass> {
    CartActivity context;
    ProgressDialog progressView;
    List<CartModel> listProduct;
    private static final String _REGISTERID = "registerid";
    AlertDialog.Builder builder;
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    int qty;

    public CartListAdapter(CartActivity context, List<CartModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        //shared preferences
        userPreference = new UserPreference(context);
        userDetails = userPreference.getUser();
        builder = new AlertDialog.Builder(context);
        userDb  = new UserDb(context);
        sqLiteDatabase = userDb.getReadableDatabase();
    }

    @NonNull
    @Override
    public CartListAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.Poductclass holder, final int position) {

        final CartModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.priceTxt.setText("$ "+String.format("%.2f",info.getRate()));
        holder.quantityTxt.setText("Qty "+info.getQuantity());
        holder.totalTxt.setText("Total:- $ "+info.getQuantity() * info.getRate());
        qty =(int) info.getQuantity();
        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getProductImage() + ".jpg")
                .placeholder(R.drawable.ic_person)
                .into(holder.productImageView);

        holder.quantityEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Update Qty");
                builder.setCancelable(false);
                LayoutInflater inflater = context.getLayoutInflater();
                // Inflate the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view2 = inflater.inflate(R.layout.pop_up_layout_qty, null);


                final EditText ed_qty = view2.findViewById(R.id.ed_qty);
                ImageButton add = view2.findViewById(R.id.add);
                ImageButton minus = view2.findViewById(R.id.minus);
                builder.setView(add);
                builder.setView(minus);
                builder.setView(ed_qty);
                builder.setView(view2);

                ed_qty.setText(info.getQuantity() + "");
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qty++;
                        ed_qty.setText(qty + "");
                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qty > 1) {
                            qty--;

                        }
                        ed_qty.setText(String.valueOf(qty));
                    }
                });


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userDb.updateCart(info.getId(),String.valueOf(qty),sqLiteDatabase);
                        context.cartShow();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Server Response....");
                builder.setMessage("Are you sure want to delete item");
                displayAlertDelete(String.valueOf(info.getId()));
                context.cartShow();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt,quantityTxt,priceTxt,totalTxt;
        ImageView productImageView,quantityEditImg,deleteImg;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            deleteImg = view.findViewById(R.id.deleteImg);
            quantityEditImg = view.findViewById(R.id.quantityEditImg);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            totalTxt = view.findViewById(R.id.totalTxt);
            productImageView = view.findViewById(R.id.productImageView);
            itemLayout = view.findViewById(R.id.itemLayout);
        }
    }

    public void displayAlertDelete(final String productId) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDb.deleteCart(productId,sqLiteDatabase);
                context.cartShow();
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
