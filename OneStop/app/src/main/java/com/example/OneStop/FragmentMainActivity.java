package com.example.onlinestore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.onlinestore.activity.RegisterActivity;
import com.example.onlinestore.activity.StoreRegisterActivity;
import com.example.onlinestore.fragment.AddProductFragment;
import com.example.onlinestore.fragment.AddStoreFragment;
import com.example.onlinestore.fragment.ShowAllproductFragment;
import com.example.onlinestore.preference.UserPreference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


import java.util.HashMap;

public class FragmentMainActivity extends AppCompatActivity {
    private static final String _USERNAME = "username";
    private UserPreference userPreference;
    private HashMap<String, String> userDetails;
    private ViewPager viewpager;
    LinearLayout linearLayoutAdview,checkLayoutInternet;
    SmartTabLayout viewpagertab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        getSupportActionBar().setTitle("Seller");
        getSupportActionBar().setElevation(0);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Add Product", AddProductFragment.class)
                .add("All Product", ShowAllproductFragment.class)
                .create());

        viewpager = findViewById(R.id.viewpager);

        viewpagertab = findViewById(R.id.viewpagertab);
        checkLayoutInternet = findViewById(R.id.checkLayoutInternet);
        viewpager.setAdapter(adapter);
//shared preferences
        userPreference = new UserPreference(this);
        userDetails = userPreference.getUser();
        getSupportActionBar().setTitle("Seller Name : "+String.valueOf(userDetails.get(_USERNAME)));
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewpager);
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermain,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.propedit){
            Intent intent = new Intent(FragmentMainActivity.this, StoreRegisterActivity.class);
            intent.putExtra("store","store");
            startActivity(intent);
        }else if(id == R.id.logout){
            userPreference.logoutUser();
            new UserPreference(FragmentMainActivity.this).removeUser();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Do you want to Exit from the App?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }
}

