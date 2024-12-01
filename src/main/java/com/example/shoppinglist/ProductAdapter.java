package com.example.shoppinglist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> {

    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
        holder.tvProductName.setText(model.getName());
        holder.tvProductPrice.setText("PKR " + model.getPrice());
        holder.tvProductQuantity.setText("# " + model.getQuantity());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Do You Want To Delete This Product");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uId = "";
                        if (user != null)
                            uId = user.getUid();
                        firebaseDatabase.getReference().child(Database.databaseName + "/" + uId)
                                .child(getRef(position).getKey())
                                .removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                        Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "Error Occurred. Product Not Deleted", Toast.LENGTH_SHORT).show();
                                });

                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            }
        });

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_product_layout, parent, false);
        return new ProductViewHolder(v);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName, tvProductPrice, tvProductQuantity;
        ImageView ivDelete;
        public ProductViewHolder(@NonNull View v) {
            super(v);
            tvProductName = v.findViewById(R.id.tvProductName);
            tvProductPrice = v.findViewById(R.id.tvProductPrice);
            tvProductQuantity = v.findViewById(R.id.tvProductQuantity);
            ivDelete = v.findViewById(R.id.ivDelete);
        }
    }
}
