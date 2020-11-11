package com.example.onlinestore.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.onlinestore.activity.LoginActivity;

import java.util.HashMap;

public class UserPreference {

    Context context;
    SharedPreferences sharedPreferences;
    private static final String _VALUE = "value";
    private static final String _MADARSAID = "madarsaid";
    private static final String _MEMBERCOUNT = "membercount";
    private static final String _LIKECOUNT = "likecount";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String _REGISTERID = "registerid";
    private static final String _USERNAME = "username";
    private static final String _PHONENO = "phoneNo";
    private static final String _EMAIL = "email";
    private static final String _ADDRESS = "address";
    private static final String _STATE = "state";
    private static final String _CITY = "city";
    private static final String _ZIP = "zip";
    SharedPreferences.Editor editor;

    public UserPreference(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences("Userinfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public HashMap<String, String> getUser(){
        HashMap<String, String> map = new HashMap<>();
        map.put(_REGISTERID,sharedPreferences.getString(_REGISTERID,null));
        map.put(_MADARSAID,sharedPreferences.getString(_MADARSAID,null));
        map.put(_MEMBERCOUNT,sharedPreferences.getString(_MEMBERCOUNT,null));
        map.put(_LIKECOUNT,sharedPreferences.getString(_LIKECOUNT,null));
        map.put(_USERNAME,sharedPreferences.getString(_USERNAME,null));

        map.put(_PHONENO,sharedPreferences.getString(_PHONENO,null));
        map.put(_EMAIL,sharedPreferences.getString(_EMAIL,null));
        map.put(_ADDRESS,sharedPreferences.getString(_ADDRESS,null));
        map.put(_STATE,sharedPreferences.getString(_STATE,null));
        map.put(_CITY,sharedPreferences.getString(_CITY,null));
        map.put(_ZIP,sharedPreferences.getString(_ZIP,null));
        return map;
    }

    public void saveRegisterId(String registerId){
        editor.putString(_REGISTERID,registerId);
        editor.commit();
    }
    public void saveUserName(String userName){
        editor.putString(_USERNAME,userName);
        editor.commit();
    }//,email,address,state,city,zip,
    public void savePhoneNo(String phoneNo){
        editor.putString(_PHONENO,phoneNo);
        editor.commit();
    }
    public void saveEmail(String email){
        editor.putString(_EMAIL,email);
        editor.commit();
    }
    public void saveAddress(String address){
        editor.putString(_ADDRESS,address);
        editor.commit();
    }
    public void saveState(String state){
        editor.putString(_STATE,state);
        editor.commit();
    }
    public void saveCity(String city){
        editor.putString(_CITY,city);
        editor.commit();
    }
    public void saveZip(String zip){
        editor.putString(_ZIP,zip);
        editor.commit();
    }

    public void setLo(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();

    }

    // remove user
    public void removeUser(){
        sharedPreferences.edit().clear().commit();
    }
    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public void logoutUser(){
        removeUser();
//        editor.clear();
//        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}

