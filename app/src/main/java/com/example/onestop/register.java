package com.example.onestop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {


    private static final String TAG = "register";
    EditText name_reg, email_reg , password_reg, zip_reg,phone_reg;
    Button r_button;
    TextView to_login;
    String userid;
    FirebaseAuth fireAuth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        name_reg = findViewById(R.id.name_register);
        email_reg = findViewById(R.id.email_register);
        password_reg = findViewById(R.id.password_register);
        zip_reg = findViewById(R.id.zip_register);
        phone_reg =findViewById(R.id.phone_register);
        r_button  = findViewById(R.id.button_register);
        to_login = findViewById(R.id.to_login);
        progressBar = findViewById(R.id.progressBar);
        fireAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        r_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_reg.getText().toString().trim();
                final String password = password_reg.getText().toString().trim();
                final String name = name_reg.getText().toString().trim();
                final String phone = phone_reg.getText().toString();
                final String zipcode = zip_reg.getText().toString();

                if(TextUtils.isEmpty(name)){
                    name_reg.setError("Name is required");
                    return;
                }
                if(name.contains("!")|| name.contains("@") || name.contains("#")|| name.contains("$") || name.contains("%"))
                {
                    name_reg.setError("Name cannot contain special characters. Space is allowed");
                    return;
                }

                if(TextUtils.isEmpty(email))
                {
                    email_reg.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    password_reg.setError("Password is required.");
                    return;
                }
                if(password.length() < 7){
                    password_reg.setError("Password must be >= 7 characters");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    phone_reg.setError("Phone number is required.");
                    return;
                }
                if(TextUtils.isEmpty(zipcode)){
                    zip_reg.setError("Zipcode is required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fireAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(register.this, "User Created. ", Toast.LENGTH_SHORT).show();


                            //storing the data in firestone firestone database
                            userid = fireAuth.getCurrentUser().getUid();
                            DocumentReference docref = fstore.collection("Users").document(userid);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("password",password);
                            user.put("phone",phone);
                            user.put("zipcode",zipcode);

                            docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: New user has been created for" + userid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Error! "+ e.toString());
                                }
                            });

                            //if the creation is successful then the page is switched to login page
                            startActivity(new Intent(getApplicationContext(),login.class));

                        }else{
                            Toast.makeText(register.this, "Error!" + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        /*to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
    }
}*/

        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
    }
}


