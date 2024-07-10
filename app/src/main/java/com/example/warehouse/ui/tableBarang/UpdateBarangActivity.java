package com.example.warehouse.ui.tableBarang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.warehouse.R;
import com.example.warehouse.model.Barang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateBarangActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextNamaBarang, editTextStock;
    private Spinner spinnerJenisBarang;
    private ImageView imageViewGambarBarang;
    private Button buttonUpdate, buttonChooseImage;
    private Uri imageUri;
    private Barang barang;
    private String barangId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_barang);

        // Set up the action bar with a back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update Item");
            actionBar.setDisplayHomeAsUpEnabled(true); // Show the back button
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarBackground)));
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Ensure you have a black back button icon
        }

        editTextNamaBarang = findViewById(R.id.editTextNamaBarang);
        spinnerJenisBarang = findViewById(R.id.spinnerJenisBarang);
        editTextStock = findViewById(R.id.editTextStock);
        imageViewGambarBarang = findViewById(R.id.imageViewGambarBarang);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);

        // Populate the spinner with the string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_barang_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisBarang.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            barang = (Barang) intent.getSerializableExtra("barang");
            barangId = intent.getStringExtra("barangId");

            if (barang != null) {
                editTextNamaBarang.setText(barang.getNama_barang());
                editTextStock.setText(barang.getStockAsString());
                // Load the image using Glide or any image loading library
                Glide.with(this).load(barang.getGambar_barang()).into(imageViewGambarBarang);

                // Set the spinner to the correct value
                int spinnerPosition = adapter.getPosition(barang.getJenis_barang());
                spinnerJenisBarang.setSelection(spinnerPosition);
            }
        }

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonUpdate.setOnClickListener(v -> {
            if (validateInputs()) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    updateBarang(barang.getGambar_barang());
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewGambarBarang.setImageURI(imageUri);
        }
    }

    private String getUsernameFromSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getString("USERNAME", "");
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateBarang(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateBarangActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(UpdateBarangActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String stock = editTextStock.getText().toString().trim();

        if (TextUtils.isEmpty(namaBarang)) {
            editTextNamaBarang.setError("Nama Barang is required");
            return false;
        }

        if (TextUtils.isEmpty(stock)) {
            editTextStock.setError("Stock is required");
            return false;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue <= 0) {
                editTextStock.setError("Stock must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextStock.setError("Stock must be a valid number");
            return false;
        }

        return true;
    }

    private void updateBarang(String imageUrl) {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String jenisBarang = spinnerJenisBarang.getSelectedItem().toString();
        String stock = editTextStock.getText().toString().trim();
        String username = getUsernameFromSession();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);
        Barang updatedBarang = new Barang();
        updatedBarang.setGambar_barang(imageUrl);
        updatedBarang.setJenis_barang(jenisBarang);
        updatedBarang.setNama_barang(namaBarang);
        updatedBarang.setStock(stock);

        databaseReference.setValue(updatedBarang)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateBarangActivity.this, "Barang updated", Toast.LENGTH_SHORT).show();

                        // Ambil tanggal saat ini
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = sdf.format(new Date());

                        createTransactionHistory(barangId, "Mengupdate Barang " + namaBarang, stock, currentDate, username);
                        finish();
                    } else {
                        Toast.makeText(UpdateBarangActivity.this, "Failed to update barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createTransactionHistory(String idBarang, String jenisTransaksi, String jumlah, String tanggalTransaksi, String username) {
        DatabaseReference historyReference = FirebaseDatabase.getInstance().getReference("transaksi");
        String historyId = historyReference.push().getKey();

        Map<String, Object> historyData = new HashMap<>();
        historyData.put("id_barang", idBarang);
        historyData.put("jenis_transaksi", jenisTransaksi);
        historyData.put("jumlah", jumlah);
        historyData.put("tanggal_transaksi", tanggalTransaksi);
        historyData.put("username", username);

        if (historyId != null) {
            historyReference.child(historyId).setValue(historyData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateBarangActivity.this, "Transaction history created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateBarangActivity.this, "Failed to create transaction history", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button in the action bar is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
