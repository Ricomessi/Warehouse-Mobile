package com.example.warehouse.ui.tableBarang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse.R;
import com.example.warehouse.model.Barang;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AmbilBarangActivity extends AppCompatActivity {

    private TextView textViewNamaBarang, textViewJenisBarang, textViewStokTersedia;
    private EditText editTextJumlahAmbil;
    private Button buttonAmbilBarang;
    private Barang barang;
    private String barangId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambil_barang);

        // Enable back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewNamaBarang = findViewById(R.id.textViewNamaBarang);
        textViewJenisBarang = findViewById(R.id.textViewJenisBarang);
        textViewStokTersedia = findViewById(R.id.textViewStokTersedia);
        editTextJumlahAmbil = findViewById(R.id.editTextJumlahAmbil);
        buttonAmbilBarang = findViewById(R.id.buttonAmbilBarang);

        if (getIntent() != null) {
            barang = (Barang) getIntent().getSerializableExtra("barang");
            barangId = getIntent().getStringExtra("barangId");

            if (barang != null) {
                textViewNamaBarang.setText("Nama Barang: " + barang.getNama_barang());
                textViewJenisBarang.setText("Jenis Barang: " + barang.getJenis_barang());
                textViewStokTersedia.setText("Stok Tersedia: " + barang.getStockAsString());
            }
        }

        buttonAmbilBarang.setOnClickListener(v -> {
            String jumlahAmbilStr = editTextJumlahAmbil.getText().toString().trim();
            if (TextUtils.isEmpty(jumlahAmbilStr)) {
                Toast.makeText(AmbilBarangActivity.this, "Please enter the quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int jumlahAmbil = Integer.parseInt(jumlahAmbilStr);
            int stokTersedia = Integer.parseInt(barang.getStockAsString());

            if (jumlahAmbil > stokTersedia) {
                Toast.makeText(AmbilBarangActivity.this, "Stock not sufficient", Toast.LENGTH_SHORT).show();
            } else {
                updateStok(barangId, stokTersedia - jumlahAmbil, jumlahAmbil);
            }
        });
    }

    private void updateStok(String barangId, int newStok, int jumlahAmbil) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);
        databaseReference.child("stock").setValue(newStok)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveTransactionHistory(barangId, jumlahAmbil);
                        Toast.makeText(AmbilBarangActivity.this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AmbilBarangActivity.this, "Failed to update stock", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveTransactionHistory(String barangId, int jumlahAmbil) {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("transaksi").push();

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "unknown");

        String jenisTransaksi = "Mengambil Barang " + barang.getNama_barang() + " sejumlah " + jumlahAmbil;
        String tanggalTransaksi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("id_barang", barangId);
        transactionData.put("jenis_transaksi", jenisTransaksi);
        transactionData.put("jumlah", String.valueOf(jumlahAmbil));
        transactionData.put("tanggal_transaksi", tanggalTransaksi);
        transactionData.put("username", username);

        transactionRef.setValue(transactionData)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(AmbilBarangActivity.this, "Failed to save transaction history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
