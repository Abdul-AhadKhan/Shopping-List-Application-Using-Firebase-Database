package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private TextView signUp, forgotPassword;
    private TextInputEditText txtEmail, txtPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void init(){
        signUp = findViewById(R.id.tvSignUp);
        forgotPassword = findViewById(R.id.tvForgotPassword);
        txtEmail = findViewById(R.id.LoginEmail);
        txtPassword = findViewById(R.id.LoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        handler = new Handler();
    }

    private void login(){
        String email = "", password = "";
        email = Objects.requireNonNull(txtEmail.getText()).toString().trim();
        password = Objects.requireNonNull(txtPassword.getText()).toString().trim();
        if (email.isEmpty()){
            Toast.makeText(Login.this, "Kindly Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(Login.this, "Kindly Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null){
                        if (firebaseUser.isEmailVerified()){
                            startActivity(new Intent(Login.this, Dashboard.class));
                            finish();
                        }
                        else {
                            Toast.makeText(Login.this, "Your Email Is Not Verified. A Verfication Email Has Been" +
                                    "Sent To Your Email Account. Kindly Verify Yourself", Toast.LENGTH_SHORT).show();
                            startCheckingEmailVerification();
                        }
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
                                startActivity(new Intent(Login.this, Dashboard.class));
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