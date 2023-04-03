package com.example.placowkamedycznajava;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class UserFragment extends Fragment implements UserAppointmentAdapter.OnClickListener {

    RecyclerView recyclerView;
    UserAppointmentAdapter adapter;
    ArrayList<UserAppointment> appointmentArrayList;
    ProgressBar progressBar;
    ItemTouchHelper.SimpleCallback swipeCallback;
    TextView recyclerInfoView;

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        progressBar = view.findViewById(R.id.user_panel_progressBar);
        recyclerView = view.findViewById(R.id.userPanelRecyclerView);
        recyclerInfoView = view.findViewById(R.id.recycler_info_view);
        appointmentArrayList = new ArrayList<>();
        initRecycler();
        getUserAppointments();
        initSwipeCallback();

        return view;
    }

    private void getUserAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        DataService dataService = new DataService(getContext());
        dataService.getUserAppointments(String.valueOf(MainActivity.userID), new DataService.UserAppointmentsResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONArray usersAppointments = response.getJSONArray("wizyty");
                    for (int i = 0; i < usersAppointments.length(); i++) {
                        JSONObject appointment = usersAppointments.getJSONObject(i);
                        appointmentArrayList.add(new UserAppointment(
                                appointment.getInt("id"),
                                DatesQueryHelper.formatDateTime(appointment.getString("termin")),
                                appointment.getString("personel"),
                                appointment.getString("specjalnosc")
                        ));
                    }
                    adapter.notifyItemRangeInserted(0, appointmentArrayList.size());
                    progressBar.setVisibility(View.GONE);
                    checkIfEmpty();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error happened during processing the data", Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    Toast.makeText(getContext(), "Error happened during processing the datetime from server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error happened during communication with server", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                System.out.println(message);
            }
        });

    }

    private void initRecycler() {
        adapter = new UserAppointmentAdapter(appointmentArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }


    // overrides recycleritem onclicks ------------------
    @Override
    public void onItemCLick(int position) {
        // jeszcze nie wiem co tutaj
    }

    @Override
    public void onDeleteClick(int position) {
        deleteUserAppointment(position);

    }
    // --------------------------------------------------

    private void deleteUserAppointment(int position) {
        new DataService(getContext()).deleteUserAppointment(
                String.valueOf(appointmentArrayList.get(position).getId()),
                new DataService.AppointmentDeleteResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        appointmentArrayList.remove(position);
                        adapter.notifyItemRemoved(position);
                        checkIfEmpty();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getContext(), "Error happened on the server side", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initSwipeCallback() {
        swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteUserAppointment(position);
            }
        };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    private void checkIfEmpty() {
        if (appointmentArrayList.isEmpty()) recyclerInfoView.setVisibility(View.VISIBLE);
        else recyclerInfoView.setVisibility(View.GONE);
    }
}