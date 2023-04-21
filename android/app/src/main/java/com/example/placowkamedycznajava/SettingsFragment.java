package com.example.placowkamedycznajava;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {

    private final int REQUEST_CODE = 1;
    Button personal_settigns, account_settings;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        personal_settigns = view.findViewById(R.id.personal_settings);
        account_settings = view.findViewById(R.id.account_settings);

        personal_settigns.setOnClickListener(button -> {
            startActivity(new Intent(getContext(), PersonalSettingsActivity.class));
        });

        account_settings.setOnClickListener(button -> {
            startActivity(new Intent(getContext(), AccountSettingsActivity.class));
            startActivityForResult(new Intent(getContext(), AccountSettingsActivity.class), REQUEST_CODE);
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            boolean ifDelete = data.getBooleanExtra("delete", false);
            if (ifDelete) {
                // perform some operation
            } else {
                // perform some other operation
            }
        }
    }
}