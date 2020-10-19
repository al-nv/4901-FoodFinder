package com.example.onestop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class login extends AppCompatActivity {


    EditText nemail, npassword;
    Button nlogin, ntoregisterforbuyer, ntoregisterforseller;
    TextView ntoregister,forgot;
    ProgressBar progressBar;
    FirebaseAuth fireAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    final EditText nemail, npassword;
    Button nlogin, ntoregisterforbuyer, ntoregisterforseller;
    final TextView ntoregister, forgot;
    final ProgressBar progressBar;
    final FirebaseAuth fireAuth;


        nemail = findViewById(R.id.email_login);
        npassword = findViewById(R.id.password_login);
        nlogin = findViewById(R.id.button_login);
        ntoregisterforbuyer = findViewById(R.id.register1);
        ntoregisterforseller = findViewById(R.id.register2);
        progressBar = findViewById(R.id.progressBar2);
        forgot = findViewById(R.id.tv_forgot);
        fireAuth = FirebaseAuth.getInstance();


        /*ntoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(login.this,register.class);
                startActivity(a);
            }
        });
*/
        ntoregisterforbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(login.this,register.class);
                startActivity(a);
            }
        });

        ntoregisterforseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(login.this,register.class);
                startActivity(a);
            }
        });

        nlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = nemail.getText().toString().trim();
                String password = npassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    nemail.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    npassword.setError("Password is required.");
                    return;
                }

                if (password.length() < 7) {
                    npassword.setError("Password must be greater then 7 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //Authorise the; firebase
                fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Login Successful ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {

                            Toast.makeText(login.this, "Error!" + task.getException(). getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });


        /*ntoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), register.class));
            }
        });*/


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText reset = new EditText(view.getContext());
                final AlertDialog.Builder pswdResetDialog = new AlertDialog.Builder(view.getContext());
                pswdResetDialog.setTitle("Reset Password?");
                pswdResetDialog.setMessage("Enter valid email to receive link to reset password ");
                pswdResetDialog.setView(reset);

                pswdResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String mail = reset.getText().toString();
                        fireAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this,"Link to reset password sent to email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this,"Error! Reset link could not be sent"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                pswdResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                pswdResetDialog.create().show();
            }
        });
    }
}
