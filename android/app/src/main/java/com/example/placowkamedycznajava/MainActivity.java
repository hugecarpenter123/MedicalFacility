package com.example.placowkamedycznajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AppointmentSearchFragment.AppointmentSearchFragmentListener {

    int userID;
    DrawerLayout drawerLayout;
    FrameLayout fragment_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1.1
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2.1 ustaw ikonę do togglowania navigiation, i połącz z gotową funkcją (animacje)
        drawerLayout = findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar ,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 3.2
        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        // 4.1 inicjaluzuj navigation, do którego będzie podpięty itemClick listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 4.3 ustaw domyślnie jakiś fragment
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
//        navigationView.setCheckedItem(R.id.chat_fragment);

        // 5.1 wczytaj id zalogowanego użytkownika
        loadData();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        userID = sharedPreferences.getInt(LoginActivity.LOGGED_USER_ID, -1);
        // probably unnecessary
        if (userID == -1) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(MainActivity.this, item.getTitle() + " clicked()", Toast.LENGTH_SHORT).show();
        return true;
    }

    // 2.2, jeśli drawer jest otwarty to najpierw go zamknij, poźniej super()
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_appointments:
                Toast.makeText(MainActivity.this, "nav_appointments clicked()", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentSearchFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_userPanel:
                Toast.makeText(MainActivity.this, "nav_userPanel clicked()", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(MainActivity.this, "nav_settings clicked()", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(MainActivity.this, "nav_logout clicked()", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onSearchClick(HashMap<String, String> getParams) {
        // open new fragment
        AppointmentListFragment listFragment = new AppointmentListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);

        // call function on that fragment that will request appointments with params from previous Fragment
        listFragment.callForAppointments(getParams);

        Toast.makeText(this, "params: " + getParams, Toast.LENGTH_LONG).show();
    }
}