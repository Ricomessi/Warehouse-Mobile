package com.example.holoners;

import Talent.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.AdapterView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.TalentAdapter; // Assuming you have a custom adapter for Talent
import auth.ActivityLogin;
import model.Talent; // Assuming you have a Talent class

public class TalentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView listView;
    private Button btnAdd;
    private TalentAdapter adapter; // Assuming you have a custom adapter for Talent
    private ArrayList<Talent> talentList;
    private DatabaseReference dbTalent; // Adjusted for Talent

    private EditText edtSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talent);

        // List talent
        talentList = new ArrayList<>(); // Move the initialization here

        edtSearch = findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the talent list based on the search query
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        listView = findViewById(R.id.lv_list);
        FloatingActionButton btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        // Initialize the adapter after setContentView
        adapter = new TalentAdapter(this, talentList); // Assuming you have a TalentAdapter
        listView.setAdapter(adapter);

        // Set the item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Talent selectedTalent;

                // Check if the adapter is   using filteredList or talentList
                if (adapter instanceof TalentAdapter) {
                    selectedTalent = (Talent) ((TalentAdapter) adapter).getItem(i);
                } else {
                    selectedTalent = talentList.get(i);
                }

                Intent intent = new Intent(TalentActivity.this, DetailActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_TALENT, selectedTalent);
                startActivity(intent);
            }

        });



        // Initialize the database reference for Talent
        dbTalent = FirebaseDatabase.getInstance().getReference("talent"); // Adjusted for Talent

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

// Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.talent);

// Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.music) {
                    startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.talent) {
                    return true;
                } else if (item.getItemId() == R.id.article) {
                    startActivity(new Intent(getApplicationContext(), ArticleActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });



    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add) {
            Intent intent = new Intent(TalentActivity.this, CreateActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbTalent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                talentList.clear();

                for (DataSnapshot talentSnapshot : dataSnapshot.getChildren()) {
                    Talent talent = talentSnapshot.getValue(Talent.class);
                    talentList.add(talent);
                }

                // Use the global adapter to update data
                adapter.setTalentList(talentList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TalentActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            // Panggil method logout
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear session and log out user


        // Start login activity and finish current activity
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities on top of login activity
        startActivity(intent);
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Ubah Intent untuk memanggil DetailActivity
        Intent intent = new Intent(TalentActivity.this, DetailActivity.class);
        intent.putExtra("talentId", talentList.get(i).getId());
        startActivity(intent);
    }
}
