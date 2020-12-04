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
import com.example.onlinestore.activity.ProductActivity;
import com.example.onlinestore.global.Constants;
import com.example.onlinestore.gps.GPSTracker;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.model.ProductModel;
import com.example.onlinestore.preference.UserPreference;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.Poductclass> {
    ProductActivity context;
    List<ProductModel> listProduct;
    UserDb userDb;
    UserPreference userPreference;
    private HashMap<String, String> userDetails;
    SQLiteDatabase sqLiteDatabase;
    private static final String _REGISTERID = "registerid";
    GPSTracker gpsTracker;

    public UserProductAdapter(ProductActivity context, List<ProductModel> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        userDb = new UserDb(context);
        userPreference = new UserPreference(context);
        userDetails = userPreference.getUser();
        gpsTracker = new GPSTracker(context);
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

//        if (info.getStatus().equals("on")) {
//            holder.avalableTxt.setText("Available");
//            holder.avalableTxt.setBackgroundColor(R.color.green);
//        } else {
//            holder.avalableTxt.setText("N/A");
//            holder.avalableTxt.setBackgroundColor(R.color.black);
//        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddtoCartactivity.class);
                intent.putExtra("productName",info.getProductName());
                intent.putExtra("productId",info.getProductId());
                intent.putExtra("StockInQuantity",info.getQuantity());
                intent.putExtra("productPrice",info.getPrice());
                intent.putExtra("productImage",info.getProductImage());
                context.startActivity(intent);
            }
        });
        holder.favariteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long tsLong = System.currentTimeMillis()/1000;
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                sqLiteDatabase = userDb.getReadableDatabase();
                List<CartModel> listIdFavorite = userDb.retrieveFavoriteById(info.getProductId(), sqLiteDatabase);
                if(listIdFavorite.size() > 0){
                    Toast.makeText(context, "Already exits this item in a Favorite", Toast.LENGTH_SHORT).show();
                }else {
                    double tAmt = Double.parseDouble(info.getQuantity()) * Double.parseDouble(info.getPrice());//  userDb.insertCart(getIntent().getStringExtra("productId"), com.store.acmestore.global.Constants.storeId,String.valueOf(userDetails.get(_REGISTERID)),getIntent().getStringExtra("productName"), productQty, getIntent().getStringExtra("productPrice"),tAmt, String.valueOf(formatter.format(date)), String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()), getIntent().getStringExtra("productImage"), sqLiteDatabase);
                    userDb.insertFavorite(info.getProductId(), Constants.storeId,Constants.storeName,String.valueOf(userDetails.get(_REGISTERID)),info.getProductName(),info.getQuantity(),info.getQuantity(),info.getPrice(),tAmt, String.valueOf(formatter.format(date)), String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()), info.getProductImage(), sqLiteDatabase);
                    Toast.makeText(context, "successfully add to favorite", Toast.LENGTH_LONG).show();
                    showNotificationSpecificUser();
                    // context.showfavarite();
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

    public void showNotificationSpecificUser()
    {
        OneSignal.sendTag("Favarite User_ID",userDetails.get(_REGISTERID));
        OSPermissionSubscriptionState states = OneSignal.getPermissionSubscriptionState();
        String UserId = states.getSubscriptionStatus().getUserId();
        boolean isSubcribed = states.getSubscriptionStatus().getSubscribed();
        if(!isSubcribed)
            return;
        try {
            JSONObject notificationContent = new JSONObject(
                    "{'contents': {'en': 'Your Favorite Product'}," +
                            "'include_player_ids': ['"+UserId+"'], " +
                            "'headings': {'en': 'Favorite Product'}," +
                            "'app_id': \"56e53808-fe83-4174-8aef-ac507a7e5b75\""+
                            "}");
            OneSignal.postNotification(notificationContent,null);
        }catch (JSONException e){

        }


    }
}

