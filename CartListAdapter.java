package com.example.onlinestore.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.Poductclass> {
    Context context;
    ProgressDialog progressView;
    List<CartModel> listProduct;
    private static final String _REGISTERID = "registerid";
    AlertDialog.Builder builder;
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;

    public CartListAdapter(Context context, List<CartModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        //shared preferences
        userPreference = new UserPreference(context);
        userDetails = userPreference.getUser();
        builder = new AlertDialog.Builder(context);
    }

    @NonNull
    @Override
    public CartListAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.Poductclass holder, int position) {

        final CartModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.priceTxt.setText("Rs "+String.format("%.2f",info.getPrice()));
        holder.colorTxt.setVisibility(View.GONE);
        holder.quantityTxt.setText("Qty "+info.getQty());
        holder.avalableTxt.setText("Total "+info.getQty() * info.getPrice());

        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getUrlImage() + ".jpg")
                .placeholder(R.drawable.ic_person)
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt,colorTxt,quantityTxt,priceTxt,avalableTxt;
        ImageView productImageView;
        LinearLayout itemLayout;
        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            colorTxt = view.findViewById(R.id.colorTxt);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            avalableTxt = view.findViewById(R.id.avalableTxt);
            productImageView = view.findViewById(R.id.productImageView);
            itemLayout = view.findViewById(R.id.itemLayout);
        }
    }
}

