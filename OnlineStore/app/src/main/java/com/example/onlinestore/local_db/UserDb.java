package com.example.onlinestore.local_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.onlinestore.model.CartModel;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjeev on 1/16/2018.
 */

public class UserDb extends SQLiteOpenHelper {
    // name of database and  database version.


    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private List<String> loginCredentialsArrayList;
    //this query create table for ordered report


    //bookmark cart;
    private static final String _CART = "create table cart (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "productId varchar(150)," +
            "productName varchar(150)," +
            "qty varchar(50)," +
            "price varchar(50)," +
            "orderDate varchar(50)," +
            "orderNo varchar(50)," +
            "urlImage varchar(500))";
    //bookmark favaroute;
    private static final String _FAVORITE = "create table favorite (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "productId varchar(150)," +
            "productName varchar(150)," +
            "qty varchar(50)," +
            "price varchar(50)," +
            "orderDate varchar(50)," +
            "orderNo varchar(50)," +
            "urlImage varchar(500))";
    // constructor
    public UserDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("Database operation", "database created");



    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(_CART);
        db.execSQL(_FAVORITE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //cart
    public void insertCart(String productId,String productName,String qty,String price,String orderDate,String orderNo,String urlImage,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("productId",productId);
        contentValues.put("productName",productName);
        contentValues.put("qty",qty);
        contentValues.put("price",price);
        contentValues.put("orderDate",orderDate);
        contentValues.put("orderNo",orderNo);
        contentValues.put("urlImage",urlImage);
        db.insert("cart", null, contentValues);
    }
    //recive cart
    public List<CartModel> retrieveCart(SQLiteDatabase db){
        List<CartModel> list = new ArrayList<>();
        String sql = "select * from cart";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {


                CartModel info = new CartModel();
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductId(cursor.getString(cursor.getColumnIndex("productId")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                info.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
                info.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("orderDate")));
                info.setOrderNo(cursor.getString(cursor.getColumnIndex("orderNo")));
                info.setUrlImage(cursor.getString(cursor.getColumnIndex("urlImage")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    // retrive dublicate data
    //recive cart by id
    public List<CartModel> retrieveCartById(String productId,SQLiteDatabase db){
        List<CartModel> list = new ArrayList<>();
        String sql = "select * from cart where productId ='"+productId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                CartModel info = new CartModel();
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                info.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
                info.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("orderDate")));
                info.setOrderNo(cursor.getString(cursor.getColumnIndex("orderNo")));
                info.setUrlImage(cursor.getString(cursor.getColumnIndex("urlImage")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    // update extra
    public void updateCart( int id,String qty, SQLiteDatabase db) {
        String sql = "update cart set qty ='" + qty + "' where id='" + id + "'";
        db.execSQL(sql);
    }

    public void deleteCart(String id,SQLiteDatabase db){
        String sql = "delete from cart where id ='"+id+"'";
        db.execSQL(sql);
    }
    public void deleteCartAll(SQLiteDatabase db){
        String sql = "delete from cart";
        db.execSQL(sql);
    }

    //cart
    public void insertFavorite(String productId,String productName,String qty,String price,String orderDate,String orderNo,String urlImage,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("productId",productId);
        contentValues.put("productName",productName);
        contentValues.put("qty",qty);
        contentValues.put("price",price);
        contentValues.put("orderDate",orderDate);
        contentValues.put("orderNo",orderNo);
        contentValues.put("urlImage",urlImage);
        db.insert("favorite", null, contentValues);
    }
    //recive cart
    public List<CartModel> retrieveFavorite(SQLiteDatabase db){
        List<CartModel> list = new ArrayList<>();
        String sql = "select * from favorite";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                CartModel info = new CartModel();
                info.setProductId(cursor.getString(cursor.getColumnIndex("productId")));
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                info.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
                info.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("orderDate")));
                info.setOrderNo(cursor.getString(cursor.getColumnIndex("orderNo")));
                info.setUrlImage(cursor.getString(cursor.getColumnIndex("urlImage")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    //recive cart by id
    public List<CartModel> retrieveFavoriteById(String productId,SQLiteDatabase db){
        List<CartModel> list = new ArrayList<>();
        String sql = "select * from favorite where productId ='"+productId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                CartModel info = new CartModel();
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
                info.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
                info.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("orderDate")));
                info.setOrderNo(cursor.getString(cursor.getColumnIndex("orderNo")));
                info.setUrlImage(cursor.getString(cursor.getColumnIndex("urlImage")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    // update extra
    public void updateFavorite( int id,String qty, SQLiteDatabase db) {
        String sql = "update favorite set qty ='" + qty + "' where id='" + id + "'";
        db.execSQL(sql);
    }

    public void deleteFavorite(String id,SQLiteDatabase db){
        String sql = "delete from favorite where id ='"+id+"'";
        db.execSQL(sql);
    }
}

