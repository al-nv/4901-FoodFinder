package com.example.onlinestore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinestore.MainActivity;
import com.example.onlinestore.R;
import com.example.onlinestore.local_db.UserDb;
import com.example.onlinestore.model.CartModel;
import com.example.onlinestore.preference.UserPreference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddtoCartactivity extends AppCompatActivity {
    private static final String _REGISTERID = "registerid";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    AlertDialog.Builder builder;
    ProgressDialog progressView;
    ImageView productImageView,minusImageView,addImageView;
    TextView productNameTxt,productPriceTxt,qtyTxt;
    String productId,productName,productQty,productPrice,productImage;
    int count=1;
    double totalAmt;
    String urlImage;
    private Bitmap bitmap;

    UserDb userDb;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_cartactivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("productName"));
        productImageView = findViewById(R.id.productImageView);
        productNameTxt = findViewById(R.id.productNameTxt);
        productPriceTxt = findViewById(R.id.productPriceTxt);
        qtyTxt = findViewById(R.id.qtyTxt);
        addImageView = findViewById(R.id.addImageView);
        minusImageView = findViewById(R.id.minusImageView);
        //shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();

        userDb = new UserDb(this);
        sqLiteDatabase = userDb.getReadableDatabase();

        productNameTxt.setText(getIntent().getStringExtra("productName"));
        productPriceTxt.setText(getIntent().getStringExtra("productPrice"));
        //  qtyTxt.setText(getIntent().getStringExtra("productQty"));

        Glide.with(AddtoCartactivity.this)
                .load("http://192.168.56.1/storeImage/"+getIntent().getStringExtra("productImage")+".jpg")
                .placeholder(R.drawable.ic_person)
                .into(productImageView);

        urlImage = "http://192.168.56.1/storeImage/"+getIntent().getStringExtra("productImage")+".jpg";
        qtyTxt.setText(String.valueOf(count));
        //  count = Integer.parseInt(getIntent().getStringExtra("productQty"));
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAmt=0;
                count++;
                totalAmt = Double.parseDouble(getIntent().getStringExtra("productPrice")) * count;
                qtyTxt.setText(String.valueOf(count));
                // productPriceTxt.setText(String.valueOf("Rs :"+totalAmt));
                productPriceTxt.setText(String.format("Rs : "+"%.2f",totalAmt));
            }
        });
        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAmt=0;
                if(count > 1){
                    count--;

                }
                totalAmt = Double.parseDouble(getIntent().getStringExtra("productPrice")) * count;
                qtyTxt.setText(String.valueOf(count));
                //  productPriceTxt.setText("Rs :"+String.valueOf(totalAmt));
                productPriceTxt.setText(String.format("Rs : "+"%.2f",totalAmt));
            }
        });
    }

    public void addToBtn(View view) {
        productName = productNameTxt.getText().toString().trim();
        productPrice = productPriceTxt.getText().toString().trim();
        productQty = qtyTxt.getText().toString().trim();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Server Response....");
        builder.setMessage("Are you sure add to cart...");
        String code = "reg_success";
        displayAlert(code);
    }

    public void displayAlert(final String code){
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")){
                    //  progressView.dismiss();
                    Long tsLong = System.currentTimeMillis()/1000;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    // System.out.println(formatter.format(date));
                    sqLiteDatabase = userDb.getReadableDatabase();
                    List<CartModel> listIdcart = userDb.retrieveCartById(String.valueOf(getIntent().getStringExtra("productId")), sqLiteDatabase);
                    if(listIdcart.size() > 0){
                        Toast.makeText(AddtoCartactivity.this, "Already exits this item in a cart", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(AddtoCartactivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        sqLiteDatabase = userDb.getWritableDatabase();
                        userDb.insertCart(getIntent().getStringExtra("productId"), getIntent().getStringExtra("productName"), productQty, getIntent().getStringExtra("productPrice"), String.valueOf(formatter.format(date)), String.valueOf(tsLong), getIntent().getStringExtra("productImage"), sqLiteDatabase);
                    }
                }else if(code.equals("no_success")){
                    // progressView.dismiss();

                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}