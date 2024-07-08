package com.example.warehouse.ui.tableBarang;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
            if (imageUri != null) {
                uploadImage();
            } else {
                createBarang(null); // Jika tidak ada gambar yang dipilih
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
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            createBarang(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateBarangActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(CreateBarangActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void createBarang(String imageUrl) {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String jenisBarang = spinnerJenisBarang.getSelectedItem().toString();
        String stock = editTextStock.getText().toString().trim();

        if (TextUtils.isEmpty(namaBarang) || TextUtils.isEmpty(jenisBarang) || TextUtils.isEmpty(stock)) {
            Toast.makeText(CreateBarangActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        String barangId = databaseReference.push().getKey();

        // Create a map to hold the data in the required structure
        Map<String, Object> barangData = new HashMap<>();
        barangData.put("nama_barang", namaBarang);
        barangData.put("jenis_barang", jenisBarang);
        barangData.put("stock", Integer.parseInt(stock));  // assuming stock is an integer
        barangData.put("gambar_barang", imageUrl);

        // Save the data to the database with the generated key
        databaseReference.child(barangId).setValue(barangData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateBarangActivity.this, "Barang created", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear input fields after successful creation
                    } else {
                        Toast.makeText(CreateBarangActivity.this, "Failed to create barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        editTextNamaBarang.setText("");
        spinnerJenisBarang.setSelection(0);
        editTextStock.setText("");
        imageViewGambarBarang.setImageResource(R.drawable.barang);
        imageUri = null;
    }
}
