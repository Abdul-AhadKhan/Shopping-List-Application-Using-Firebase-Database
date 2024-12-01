package com.example.shoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {


    FloatingActionButton fabAddProduct;
    RecyclerView rvProducts;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String uId;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        Query query = databaseReference.child(Database.databaseName + "/" + uId);
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new ProductAdapter(options, Dashboard.this);
        rvProducts.setAdapter(adapter);
        rvProducts.setHasFixedSize(true);

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });
    }

    private void init(){
        fabAddProduct = findViewById(R.id.fabAddProduct);
        rvProducts = findViewById(R.id.rvProducts);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        if (user != null){
            uId = user.getUid();
        }
    }

    private void addProduct(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
        View v = LayoutInflater.from(Dashboard.this).inflate(R.layout.add_product_layout, null, false);
        dialog.setView(v);
        TextInputEditText txtProductName, txtProductPrice, txtProductQuantity;
        Button btnAdd;
        txtProductName = v.findViewById(R.id.tietProductName);
        txtProductPrice = v.findViewById(R.id.tietProductPrice);
        txtProductQuantity = v.findViewById(R.id.tietProductQuantity);
        btnAdd = v.findViewById(R.id.btnAddProduct);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Objects.requireNonNull(txtProductName.getText()).toString().trim().toUpperCase();
                int price = Integer.parseInt(Objects.requireNonNull(txtProductPrice.getText()).toString());
                int quantity = Integer.parseInt(Objects.requireNonNull(txtProductQuantity.getText()).toString());
                if (name.isEmpty()){
                    Toast.makeText(Dashboard.this, "Kindly Enter Product Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (price <= 0){
                    Toast.makeText(Dashboard.this, "Invalid Price", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (quantity <= 0){
                    Toast.makeText(Dashboard.this, "Invalid Quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(Database.productName, name);
                map.put(Database.productPrice, price);
                map.put(Database.productQuantity, quantity);

                databaseReference.child(Database.databaseName + "/" + uId)
                        .push()
                        .setValue(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(Dashboard.this, "Product Added", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Dashboard.this, "Could Not Add Product. Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}