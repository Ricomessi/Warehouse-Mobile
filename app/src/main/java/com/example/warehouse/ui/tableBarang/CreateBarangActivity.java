package com.example.warehouse.ui.tableBarang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse.R;
import com.example.warehouse.model.Barang;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateBarangActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextNamaBarang, editTextStock;
    private Spinner spinnerJenisBarang;
    private ImageView imageViewGambarBarang;
    private Button buttonCreate, buttonChooseImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_barang);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Create Item");
            actionBar.setDisplayHomeAsUpEnabled(true); // Show the back button
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarBackground)));
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Ensure you have a black back button icon
        }

        editTextNamaBarang = findViewById(R.id.editTextNamaBarang);
        editTextStock = findViewById(R.id.editTextStock);
        spinnerJenisBarang = findViewById(R.id.spinnerJenisBarang);
        imageViewGambarBarang = findViewById(R.id.imageViewGambarBarang);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);

        // Initialize Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_barang_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisBarang.setAdapter(adapter);

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonCreate.setOnClickListener(v -> {
            if (validateInputs()) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    checkAndCreateBarang(null); // If no image is selected
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click here
        onBackPressed();
        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewGambarBarang.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("barang");
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            checkAndCreateBarang(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateBarangActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(CreateBarangActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUsernameFromSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getString("USERNAME", null);
    }

    private void createTransactionHistory(String idBarang, String jenisTransaksi, String jumlah, String tanggal, String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transaksi");
        String transactionId = databaseReference.push().getKey();

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("id_barang", idBarang);
        transactionData.put("jenis_transaksi", jenisTransaksi);
        transactionData.put("jumlah", jumlah);
        transactionData.put("tanggal_transaksi", tanggal);
        transactionData.put("username", username);

        databaseReference.child(transactionId).setValue(transactionData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateBarangActivity.this, "Transaction history created", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateBarangActivity.this, "Failed to create transaction history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndCreateBarang(String imageUrl) {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String jenisBarang = spinnerJenisBarang.getSelectedItem().toString();
        String stock = editTextStock.getText().toString().trim();
        String username = getUsernameFromSession();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");

        databaseReference.orderByChild("nama_barang").equalTo(namaBarang).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If item with the same name exists, update its stock
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Barang existingBarang = snapshot.getValue(Barang.class);
                        if (existingBarang != null) {
                            String existingBarangId = snapshot.getKey();
                            int updatedStock = Integer.parseInt(existingBarang.getStock().toString()) + Integer.parseInt(stock);
                            updateBarangStock(existingBarangId, updatedStock, namaBarang, stock, username);
                        }
                    }
                } else {
                    // If item does not exist, create a new one
                    createNewBarang(imageUrl, namaBarang, jenisBarang, stock, username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CreateBarangActivity.this, "Failed to check item: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBarangStock(String barangId, int updatedStock, String namaBarang, String stock, String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("stock", updatedStock);

        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateBarangActivity.this, "Stock updated", Toast.LENGTH_SHORT).show();

                // Ambil tanggal saat ini
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(new Date());

                createTransactionHistory(barangId, "Menambah Stok Barang " + namaBarang, stock, currentDate, username);
                clearFields(); // Clear input fields after successful update
            } else {
                Toast.makeText(CreateBarangActivity.this, "Failed to update stock", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewBarang(String imageUrl, String namaBarang, String jenisBarang, String stock, String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        String barangId = databaseReference.push().getKey();

        Map<String, Object> barangData = new HashMap<>();
        barangData.put("nama_barang", namaBarang);
        barangData.put("jenis_barang", jenisBarang);
        barangData.put("stock", Integer.parseInt(stock));  // assuming stock is an integer
        barangData.put("gambar_barang", imageUrl);

        databaseReference.child(barangId).setValue(barangData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateBarangActivity.this, "Barang created", Toast.LENGTH_SHORT).show();

                        // Ambil tanggal saat ini
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = sdf.format(new Date());

                        createTransactionHistory(barangId, "Menambah Barang " + namaBarang, stock, currentDate, username);
                        clearFields(); // Clear input fields after successful creation
                    } else {
                        Toast.makeText(CreateBarangActivity.this, "Failed to create barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs() {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String stock = editTextStock.getText().toString().trim();

        if (TextUtils.isEmpty(namaBarang)) {
            editTextNamaBarang.setError("Nama Barang is required");
            return false;
        }

        if (spinnerJenisBarang.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a Jenis Barang", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(stock)) {
            editTextStock.setError("Stock is required");
            return false;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue <= 0) {
                editTextStock.setError("Stock must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextStock.setError("Invalid stock value");
            return false;
        }

        return true;
    }

    private void clearFields() {
        editTextNamaBarang.setText("");
        spinnerJenisBarang.setSelection(0);
        editTextStock.setText("");
        imageViewGambarBarang.setImageResource(R.drawable.barang);
        imageUri = null;
    }
}
