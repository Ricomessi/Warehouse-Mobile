package com.example.warehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.warehouse.databinding.ActivityNavigationBinding;
import com.google.android.material.navigation.NavigationView;
import android.content.SharedPreferences;

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
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");
        String name = sharedPreferences.getString("NAME", "");
        String role = sharedPreferences.getString("ROLE", "");

        Log.d(TAG, "Received username: " + username);
        Log.d(TAG, "Received name: " + name);
        Log.d(TAG, "Received role: " + role);

        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("ROLE", role);
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
        } else if (id == R.id.nav_profile) {
            // Navigate to ProfileActivity
            Intent intent = new Intent(NavigationActivity.this, ProfileActivity.class);
            startActivity(intent);
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
