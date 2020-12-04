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


    //bookmark cart;ProductId,StoreId,UserId,Quantity,Rate,Amount,OrderDate,Latitude,Longitude
    private static final String _CART = "create table cart (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ProductId varchar(10)," +
            "StoreId varchar(10)," +
            "UserId varchar(10)," +
            "StoreName varchar(100)," +
            "ProductName varchar(100)," +
            "StockInQuantity varchar(50)," +
            "Quantity varchar(50)," +
            "Rate varchar(50)," +
            "Amount varchar(50)," +
            "OrderDate varchar(100)," +
            "Latitude varchar(50)," +
            "Longitude varchar(50),"+
            "productImage varchar(500))";
    //bookmark favaroute;
    private static final String _FAVORITE = "create table favorite (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ProductId varchar(10)," +
            "StoreId varchar(10)," +
            "UserId varchar(10)," +
            "StoreName varchar(100)," +
            "ProductName varchar(100)," +
            "StockInQuantity varchar(50)," +
            "Quantity varchar(50)," +
            "Rate varchar(50)," +
            "Amount varchar(50)," +
            "OrderDate varchar(100)," +
            "Latitude varchar(50)," +
            "Longitude varchar(50),"+
            "productImage varchar(500))";
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

    //cart ProductId,StoreId,UserId,Quantity,Rate,Amount,OrderDate,Latitude,Longitude
    public void insertCart(String ProductId,String StoreId,String storeName,String UserId,String productName,String StockInQuantity,String Quantity,String Rate,double Amount,String OrderDate,String Latitude,String Longitude,String productImage,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ProductId",ProductId);
        contentValues.put("StoreId",StoreId);
        contentValues.put("UserId",UserId);
        contentValues.put("StoreName",storeName);
        contentValues.put("ProductName",productName);
        contentValues.put("StockInQuantity",StockInQuantity);
        contentValues.put("Quantity",Quantity);
        contentValues.put("Rate",Rate);
        contentValues.put("Amount",Amount);
        contentValues.put("OrderDate",OrderDate);
        contentValues.put("Latitude",Latitude);
        contentValues.put("Longitude",Longitude);
        contentValues.put("productImage",productImage);
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
                //  ProductId,StoreId,UserId,Quantity,Rate,Amount,OrderDate,Latitude,Longitude
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductId(cursor.getString(cursor.getColumnIndex("ProductId")));
                info.setStoreId(cursor.getString(cursor.getColumnIndex("StoreId")));
                info.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                info.setStoreName(cursor.getString(cursor.getColumnIndex("StoreName")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("ProductName")));
                info.setStockInQuantity(cursor.getDouble(cursor.getColumnIndex("StockInQuantity")));
                info.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                info.setRate(cursor.getDouble(cursor.getColumnIndex("Rate")));
                info.setAmount(cursor.getDouble(cursor.getColumnIndex("Amount")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("OrderDate")));
                info.setLatitude(cursor.getString(cursor.getColumnIndex("Latitude")));
                info.setLongitude(cursor.getString(cursor.getColumnIndex("Longitude")));
                info.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
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
        String sql = "select * from cart where ProductId ='"+productId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                CartModel info = new CartModel();
                info.setProductId(cursor.getString(cursor.getColumnIndex("ProductId")));
                info.setStoreId(cursor.getString(cursor.getColumnIndex("StoreId")));
                info.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                info.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                info.setRate(cursor.getDouble(cursor.getColumnIndex("Rate")));
                info.setAmount(cursor.getDouble(cursor.getColumnIndex("Amount")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("OrderDate")));
                info.setLatitude(cursor.getString(cursor.getColumnIndex("Latitude")));
                info.setLongitude(cursor.getString(cursor.getColumnIndex("Longitude")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    //recive cart by id
    public List<CartModel> retrieveCartByStoreId(String storeId,SQLiteDatabase db){
        List<CartModel> list = new ArrayList<>();
        String sql = "select * from cart where StoreId ='"+storeId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                CartModel info = new CartModel();
                info.setProductId(cursor.getString(cursor.getColumnIndex("ProductId")));
                info.setStoreId(cursor.getString(cursor.getColumnIndex("StoreId")));
                info.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                info.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                info.setRate(cursor.getDouble(cursor.getColumnIndex("Rate")));
                info.setAmount(cursor.getDouble(cursor.getColumnIndex("Amount")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("OrderDate")));
                info.setLatitude(cursor.getString(cursor.getColumnIndex("Latitude")));
                info.setLongitude(cursor.getString(cursor.getColumnIndex("Longitude")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    // update extra
    public void updateCart( int id,String qty, SQLiteDatabase db) {
        String sql = "update cart set Quantity ='" + qty + "' where id='" + id + "'";
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
    public void insertFavorite(String ProductId,String StoreId,String UserId,String StoreName,String productName,String StockInQuantity,String Quantity,String Rate,double Amount,String OrderDate,String Latitude,String Longitude,String productImage,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ProductId",ProductId);
        contentValues.put("StoreId",StoreId);
        contentValues.put("UserId",UserId);
        contentValues.put("StoreName",StoreName);
        contentValues.put("ProductName",productName);
        contentValues.put("StockInQuantity",StockInQuantity);
        contentValues.put("Quantity",Quantity);
        contentValues.put("Rate",Rate);
        contentValues.put("Amount",Amount);
        contentValues.put("OrderDate",OrderDate);
        contentValues.put("Latitude",Latitude);
        contentValues.put("Longitude",Longitude);
        contentValues.put("productImage",productImage);
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
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setProductId(cursor.getString(cursor.getColumnIndex("ProductId")));
                info.setStoreId(cursor.getString(cursor.getColumnIndex("StoreId")));
                info.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                info.setStoreName(cursor.getString(cursor.getColumnIndex("StoreName")));
                info.setProductName(cursor.getString(cursor.getColumnIndex("ProductName")));
                info.setStockInQuantity(cursor.getDouble(cursor.getColumnIndex("StockInQuantity")));
                info.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                info.setRate(cursor.getDouble(cursor.getColumnIndex("Rate")));
                info.setAmount(cursor.getDouble(cursor.getColumnIndex("Amount")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("OrderDate")));
                info.setLatitude(cursor.getString(cursor.getColumnIndex("Latitude")));
                info.setLongitude(cursor.getString(cursor.getColumnIndex("Longitude")));
                info.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
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
                info.setProductId(cursor.getString(cursor.getColumnIndex("ProductId")));
                info.setStoreId(cursor.getString(cursor.getColumnIndex("StoreId")));
                info.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                info.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                info.setRate(cursor.getDouble(cursor.getColumnIndex("Rate")));
                info.setAmount(cursor.getDouble(cursor.getColumnIndex("Amount")));
                info.setOrderDate(cursor.getString(cursor.getColumnIndex("OrderDate")));
                info.setLatitude(cursor.getString(cursor.getColumnIndex("Latitude")));
                info.setLongitude(cursor.getString(cursor.getColumnIndex("Longitude")));
                info.setProductImage(cursor.getString(cursor.getColumnIndex("productImage")));
                list.add(info);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    // update extra
    public void updateFavorite( int id,String qty, SQLiteDatabase db) {
        String sql = "update favorite set Quantity ='" + qty + "' where id='" + id + "'";
        db.execSQL(sql);
    }

    public void deleteFavorite(String id,SQLiteDatabase db){
        String sql = "delete from favorite where id ='"+id+"'";
        db.execSQL(sql);
    }
}
