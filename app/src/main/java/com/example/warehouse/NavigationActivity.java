package com.example.warehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "NavigationActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
//        binding.appBarNavigation.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        // Get the username, name, and role from the intent
        username = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String role = getIntent().getStringExtra("ROLE");

        Log.d(TAG, "Received username: " + username);
        Log.d(TAG, "Received name: " + name);
        Log.d(TAG, "Received role: " + role);

        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("NAME", name);
        bundle.putString("ROLE", role);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        navController.navigate(R.id.nav_home, bundle);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = binding.drawerLayout;
        int id = item.getItemId();

        // Close the drawer
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_logout) {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout? You will be logged out of your account.")
                    .setPositiveButton("Yes, Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle logout action
                            Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        } else {
            // Handle other navigation item clicks here
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

            if (handled) {
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
