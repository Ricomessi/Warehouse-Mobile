package com.example.holoners;

import Article.DetailActivity;
import Article.UpdateActivity;
import Article.CreateActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.EditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import adapter.ArticleAdapter; // Assuming you have a custom adapter for Article
import auth.ActivityLogin;
import model.Article; // Assuming you have an Article class

public class ArticleActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Button btnAdd;
    private ArticleAdapter adapter; // Assuming you have a custom adapter for Article
    private ArrayList<Article> articleList;
    private DatabaseReference dbArticle; // Adjusted for Article
    private BottomNavigationView bottomNavigationView;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articleList = new ArrayList<>();

        edtSearch = findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the article list based on the search query
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

        adapter = new ArticleAdapter(this, articleList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article selectedArticle = articleList.get(i);
                Intent intent = new Intent(ArticleActivity.this, DetailActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_ARTICLE, selectedArticle);
                startActivity(intent);
                finish();
            }
        });

        dbArticle = FirebaseDatabase.getInstance().getReference("article");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.article);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.music) {
                    startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;

                } else if (item.getItemId() == R.id.talent) {
                    startActivity(new Intent(getApplicationContext(), TalentActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.article) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add) {
            Intent intent = new Intent(ArticleActivity.this, CreateActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbArticle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear();

                for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                    Article article = articleSnapshot.getValue(Article.class);
                    articleList.add(article);
                }

                adapter.setArticleList(articleList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ArticleActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
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