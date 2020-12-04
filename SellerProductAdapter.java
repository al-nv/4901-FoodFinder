package com.example.onlinestore.adapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.onlinestore.activity.AddItemActivity;
import com.example.onlinestore.model.ProductModel;
import com.example.onlinestore.preference.UserPreference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.Poductclass> {
    Context context;
    ProgressDialog progressView;
    List<ProductModel> listProduct;
    private static final String _REGISTERID = "registerid";
    AlertDialog.Builder builder;
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;

    public SellerProductAdapter(Context context, List<ProductModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        //shared preferences
        userPreference = new UserPreference(context);
        userDetails = userPreference.getUser();
        builder = new AlertDialog.Builder(context);
    }

    @NonNull
    @Override
    public SellerProductAdapter.Poductclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selletproductitem, parent, false);
        return new Poductclass(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SellerProductAdapter.Poductclass holder, int position) {

        final ProductModel info = listProduct.get(position);
        holder.productNameTxt.setText(info.getProductName());
        holder.priceTxt.setText("$ "+info.getPrice());
        holder.colorTxt.setText(info.getColor());
        holder.quantityTxt.setText("Qty "+info.getQuantity());

        Glide.with(context)
                .load("http://192.168.56.1/storeImage/" + info.getProductImage() + ".jpg")
                .placeholder(R.drawable.ic_broken)
                .into(holder.productImageView);

//        if (info.getStatus().equals("on")) {
//            holder.aSwitch.setChecked(true);
//            holder.avalableTxt.setText("Available");
//            holder.avalableTxt.setBackgroundColor(R.color.green);
//        } else {
//            holder.aSwitch.setChecked(false);
//            holder.avalableTxt.setText("N/A");
//            holder.avalableTxt.setBackgroundColor(R.color.black);
//        }

        holder.editTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName, price, color, quantity, exp_date;
                Intent intent = new Intent(context, AddItemActivity.class);
                intent.putExtra("productId", info.getProductId());
                intent.putExtra("productName", info.getProductName());
                intent.putExtra("price", info.getPrice());
                intent.putExtra("color", info.getColor());
                intent.putExtra("quantity", info.getQuantity());
                intent.putExtra("productImage", info.getProductImage());
                intent.putExtra("manufactureDate", info.getManufactureDate());
                intent.putExtra("expiryDate", info.getExpiryDate());
                context.startActivity(intent);
            }
        });

        holder.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+info.getProductId(), Toast.LENGTH_SHORT).show();
                builder.setTitle("Server Response....");
                builder.setMessage("Are you sure want to delete item");
                displayAlertDelete(info.getProductId());


            }
        });

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    builder.setTitle("Server Response....");
                    builder.setMessage("Are you sure want to product on");
                    displayAlert("on",info.getProductId());
                } else {
                    builder.setTitle("Server Response....");
                    builder.setMessage("Are you sure want to product off");
                    displayAlert("off",info.getProductId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class Poductclass extends RecyclerView.ViewHolder {
        TextView productNameTxt, colorTxt, quantityTxt, priceTxt, editTxt, deleteTxt, avalableTxt;
        ImageView productImageView;
        Switch aSwitch;

        public Poductclass(@NonNull View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            colorTxt = view.findViewById(R.id.colorTxt);
            quantityTxt = view.findViewById(R.id.quantityTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            deleteTxt = view.findViewById(R.id.deleteTxt);
            editTxt = view.findViewById(R.id.editTxt);
            avalableTxt = view.findViewById(R.id.avalableTxt);
            aSwitch = view.findViewById(R.id.switch1);
            productImageView = view.findViewById(R.id.productImageView);
        }
    }

    public void deleteProduct(final String prductId) {
        String urlRegister = "http://192.168.56.1/DeleteProductMasterStock.php";
        StringRequest requestRegister = new StringRequest(Request.Method.POST, urlRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            // profileImage.setImageResource(0);

                            // Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                param.put("ProductId", prductId);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(requestRegister);
    }

    public void updateProduct(final String status, final String productId) {
        Toast.makeText(context, ""+status, Toast.LENGTH_SHORT).show();
        progressView = new ProgressDialog(context);
        progressView.setMessage("loading...");
        progressView.setCancelable(false);
        progressView.show();
        String urlRegister = "http://www.islamictime.ramaulnews.com/updateStatus.php";
        StringRequest requestRegister = new StringRequest(Request.Method.POST, urlRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equals("status_success")) {
                                progressView.dismiss();
                                Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();

                            } else if (code.equals("no_success")) {
                                progressView.dismiss();
                                Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
                            }

                            // profileImage.setImageResource(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressView.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressView.dismiss();
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("productId", productId);
                param.put("status", status);
                param.put("registerId", String.valueOf(userDetails.get(_REGISTERID)));
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(requestRegister);
    }

    public void displayAlert(final String status, final String productId) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(context, ""+status, Toast.LENGTH_SHORT).show();
                updateProduct(status,productId);
//                if(code.equals("status_success")){
//                    progressView.dismiss();
//
//                }else if(code.equals("no_success")){
//                    progressView.dismiss();
//                }
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

                deleteProduct(productId);
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
