package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {
    
    private TextView tvLogin;
    private TextInputEditText txtEmail, txtPassword, txtConfirmPassword;
    private Button btnSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        init();
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }
    
    private void init(){
        tvLogin = findViewById(R.id.tvLogin);
        txtPassword = findViewById(R.id.RegisterPassword);
        txtConfirmPassword = findViewById(R.id.RegisterConfirmPassword);
        txtEmail = findViewById(R.id.RegisterEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        btnSignUp = findViewById(R.id.btnSignUp);
        handler = new Handler();
    }

    private void registerUser(){

        String email = "", password = "", confirmPassword = "";
        email = Objects.requireNonNull(txtEmail.getText()).toString().trim();
        password = Objects.requireNonNull(txtPassword.getText()).toString().trim();
        confirmPassword = Objects.requireNonNull(txtConfirmPassword.getText()).toString().trim();


        if (email.isEmpty()){
            Toast.makeText(Register.this, "Kindly Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(Register.this, "Kindly Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPassword.isEmpty()){
            Toast.makeText(Register.this, "Kindly Confirm Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(Register.this, "Passwords Doesn't Match", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null){
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Register.this, "Verification Email Has Been Sent To Your Email Account. Kindly Verify Your Email.", Toast.LENGTH_SHORT).show();
                                                startCheckingEmailVerification();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkEmailVerification(){
        if (firebaseUser != null){
            firebaseUser.reload()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (firebaseUser.isEmailVerified()){
                                stopCheckingEmailVerification();
                                startActivity(new Intent(Register.this, Dashboard.class));
                                finish();
                            }
                        }
                    });

        }
    }

    private void startCheckingEmailVerification(){
         runnable = new Runnable() {
            @Override
            public void run() {
                checkEmailVerification();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void stopCheckingEmailVerification(){
        if (runnable != null){
            handler.removeCallbacks(runnable);
        }
    }
}