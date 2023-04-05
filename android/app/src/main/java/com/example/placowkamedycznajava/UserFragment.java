package com.example.placowkamedycznajava;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placowkamedycznajava.utility.ApiParamNames;
import com.example.placowkamedycznajava.utility.ConnectionAgent;
import com.example.placowkamedycznajava.utility.DatesQueryHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class UserFragment extends Fragment implements UserAppointmentAdapter.OnClickListener {

    SwipeRefreshLayout refreshLayout;
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
        refreshLayout = view.findViewById(R.id.user_panel_refresh_layout);
        progressBar = view.findViewById(R.id.user_panel_progressBar);
        recyclerView = view.findViewById(R.id.userPanelRecyclerView);
        recyclerInfoView = view.findViewById(R.id.recycler_info_view);
        appointmentArrayList = new ArrayList<>();
        initRecycler();
        getUserAppointments();
        initSwipeCallback();
        initSwipeToRefresh();

        return view;
    }

    private void getUserAppointments() {
        // progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(true);
        DataService dataService = new DataService(getContext());
        dataService.getUserAppointments(String.valueOf(MainActivity.userID), new DataService.JsonObjectResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray usersAppointments = response.getJSONArray(ApiParamNames.USER_APPOINTMENTS_ARRAY);
                    appointmentArrayList.clear();
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < usersAppointments.length(); i++) {
                        JSONObject appointment = usersAppointments.getJSONObject(i);
                        appointmentArrayList.add(new UserAppointment(
                                appointment.getInt(ApiParamNames.ID),
                                DatesQueryHelper.formatDateTime(appointment.getString(ApiParamNames.USER_APPOINTMENTS_DATATIME)),
                                appointment.getString(ApiParamNames.APPOINTMENTS_PERSONEL),
                                appointment.getString(ApiParamNames.APPOINTMENTS_SPECIALITY)
                        ));
                    }
                    adapter.notifyItemRangeInserted(0, appointmentArrayList.size());
                    refreshLayout.setRefreshing(false);
                    checkIfEmpty();

                    // TODO: 05.04.2023 remove this toasts on production
                } catch (JSONException | ParseException e) {
                    Toast.makeText(getContext(), R.string.db_processing_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), R.string.db_general_error, Toast.LENGTH_LONG).show();
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
        // possible 'more info' activity implementation or some popup
    }

    @Override
    public void onDeleteClick(int position) {
        deleteUserAppointment(position);
    }
    // --------------------------------------------------

    private void deleteUserAppointment(int position) {
        new DataService(getContext()).deleteUserAppointment(
                String.valueOf(appointmentArrayList.get(position).getId()),
                new DataService.StringResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        appointmentArrayList.remove(position);
                        adapter.notifyItemRemoved(position);
                        checkIfEmpty();
                        Toast.makeText(getContext(), R.string.appointment_cancel_successfull, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getContext(), R.string.db_general_error, Toast.LENGTH_SHORT).show();
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

    private void initSwipeToRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!ConnectionAgent.isConnected(requireContext())) {
                    Toast.makeText(getContext(), R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    return;
                }
                getUserAppointments();
            }
        });
    }
}