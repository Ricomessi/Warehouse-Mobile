package com.example.holoners;

import Music.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import adapter.MusicAdapter;
import auth.ActivityLogin;
import model.Music;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Button btnAdd;
    private MusicAdapter adapter; // Assuming you have a MusicAdapter
    private ArrayList<Music> musicList;
    private DatabaseReference dbMusic; // Adjusted for Music
    private BottomNavigationView menu_bawah;
    private TextView tulisan;
    private EditText edtSearch; // Add search functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // List music
        musicList = new ArrayList<>(); // Move the initialization here

        edtSearch = findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the music list based on the search query
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
        adapter = new MusicAdapter(this, musicList); // Assuming you have a MusicAdapter
        listView.setAdapter(adapter);

        // Set the item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Music selectedMusic;

                // Check if the adapter is using filteredList or musicList
                if (adapter instanceof MusicAdapter) {
                    selectedMusic = (Music) ((MusicAdapter) adapter).getItem(i);
                } else {
                    selectedMusic = musicList.get(i);
                }

                Intent intent = new Intent(MusicActivity.this, DetailActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_MUSIC, selectedMusic);
                startActivity(intent);
                finish();
            }

        });

        // Initialize the database reference for Music
        dbMusic = FirebaseDatabase.getInstance().getReference("music"); // Adjusted for Music

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

// Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.music);

// Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.music) {
                    return true;
                } else if (item.getItemId() == R.id.talent) {
                    startActivity(new Intent(getApplicationContext(), TalentActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
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
            Intent intent = new Intent(MusicActivity.this, CreateActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbMusic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musicList.clear();

                for (DataSnapshot musicSnapshot : dataSnapshot.getChildren()) {
                    Music music = musicSnapshot.getValue(Music.class);
                    musicList.add(music);
                }

                // Use the global adapter to update data
                adapter.setMusicList(musicList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MusicActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
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


}