package com.example.warehouse.ui.tableBarang;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.databinding.FragmentGalleryBinding;
import com.example.warehouse.model.Barang;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TableBarangFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private RecyclerView recyclerView;
    private BarangAdapter adapter;
    private List<Barang> barangList;
    private EditText editTextSearch;
    private Button buttonSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        barangList = new ArrayList<>();
        adapter = new BarangAdapter(getContext(), barangList, new BarangAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(Barang barang) {
                openUpdateActivity(barang);
            }

            @Override
            public void onDeleteClick(String barangId) {
                deleteBarang(barangId);
            }
        });

        recyclerView.setAdapter(adapter);

        editTextSearch = root.findViewById(R.id.editTextSearch);
        buttonSearch = root.findViewById(R.id.buttonSearch);

        // Add TextWatcher to automate search on text change
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchBarang(query);
                } else {
                    fetchBarangData();
                }
            }
        });

        buttonSearch.setOnClickListener(view -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchBarang(query);
            } else {
                fetchBarangData();
            }
        });

        fetchBarangData();

        FloatingActionButton fabAddItem = binding.fabAddItem;
        fabAddItem.setOnClickListener(view -> openCreateActivity());

        return root;
    }

    private void openUpdateActivity(Barang barang) {
        Intent intent = new Intent(getContext(), UpdateBarangActivity.class);
        intent.putExtra("barang", barang);
        intent.putExtra("barangId", barang.getId());
        startActivity(intent);
    }

    private void openCreateActivity() {
        Intent intent = new Intent(getContext(), CreateBarangActivity.class);
        startActivity(intent);
    }

    private void deleteBarang(String barangId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barangId);
        databaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteTransactionHistory(barangId);
                    } else {
                        Toast.makeText(getContext(), "Failed to delete barang", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTransactionHistory(String barangId) {
        DatabaseReference historyReference = FirebaseDatabase.getInstance().getReference("transaksi");
        Query query = historyReference.orderByChild("id_barang").equalTo(barangId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
                Toast.makeText(getContext(), "Barang and related transactions deleted", Toast.LENGTH_SHORT).show();
                fetchBarangData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to delete related transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchBarangData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        Query query = databaseReference.orderByKey().limitToLast(50); // Adjust limit as needed
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barangList.clear();
                List<Barang> tempList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Barang barang = dataSnapshot.getValue(Barang.class);
                    if (barang != null) {
                        barang.setId(dataSnapshot.getKey()); // Ensure the ID is set
                        tempList.add(barang);
                    }
                }
                // Add items from tempList to barangList in reverse order
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    barangList.add(tempList.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBarang(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang");
        Query searchQuery = databaseReference.orderByKey().limitToLast(50); // Adjust limit as needed
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barangList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Barang barang = dataSnapshot.getValue(Barang.class);
                    if (barang != null && (barang.getNama_barang().toLowerCase().contains(query.toLowerCase())
                            || barang.getJenis_barang().toLowerCase().contains(query.toLowerCase()))) {
                        barang.setId(dataSnapshot.getKey()); // Ensure the ID is set
                        barangList.add(barang);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}