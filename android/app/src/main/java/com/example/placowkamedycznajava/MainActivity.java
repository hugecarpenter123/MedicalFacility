package com.example.placowkamedycznajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    public static int userID;
    DrawerLayout drawerLayout;
    FrameLayout fragment_container;
    Menu drawerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1.1
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2.1 ustaw ikonę do togglowania navigiation, i połącz z gotową funkcją
        drawerLayout = findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar ,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 3.2
        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        // 4.1 inicjaluzuj navigation, do którego będzie podpięty itemClick listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();

        // 4.3 ustaw domyślnie jakiś fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentSearchFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_appointments);

        // 5.1 wczytaj id zalogowanego użytkownika
        loadData();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        userID = sharedPreferences.getInt(LoginActivity.SHARED_PREF_USER_ID, -1);
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
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                unselectAllNavItems();
                break;
            case R.id.logout:
                logUserOut();
                break;
        }
        return true;
    }

    // 2.2, jeśli drawer jest otwarty to najpierw go zamknij, poźniej super()
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // if current fragment is listFragment, redirect to search Fragment
        else if (currentFragment instanceof AppointmentListFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentSearchFragment()).commit();
        }
        // if current fragment is SettingsFragment, redirect to search Fragment
        else if (currentFragment instanceof SettingsFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentSearchFragment()).commit();
        }
        else {
            showConfirmationDialog();
        }
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_appointments:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentSearchFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_userPanel:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                unselectAllNavItems();
                break;
            case R.id.nav_logout:
                logUserOut();
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

    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // yes clicked, log the user out
                        logUserOut();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing needs to happen
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void logUserOut() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        // TODO: 26.03.2023 usunąć USER_ID z sharedPreferences
    }

    private void unselectAllNavItems() {
        for (int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setChecked(false);
        }
    }
}

