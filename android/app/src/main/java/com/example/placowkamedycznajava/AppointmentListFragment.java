package com.example.placowkamedycznajava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class AppointmentListFragment extends Fragment {

    private final DataService dataService = new DataService(getContext());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_list, container, false);
        return view;
    }

    public void callForAppointments(HashMap<String, String> getParams) {
        System.out.println("callForAppointments getParams: " + getParams);

        dataService.getFilteredAppointments(new DataService.FilteredAppointmentsResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), "Response with filtered appointments", Toast.LENGTH_LONG).show();
                // TODO: 22.03.2023 populate the recyclerview with response data
                System.out.println(response);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "An error occurred while trying to connect to the server", Toast.LENGTH_LONG).show();
            }
        }, getParams);

    }
}