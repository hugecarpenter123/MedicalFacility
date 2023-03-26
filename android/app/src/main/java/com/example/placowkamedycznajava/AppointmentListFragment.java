package com.example.placowkamedycznajava;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class AppointmentListFragment extends Fragment implements AppointmentAdapter.OnClickListener {
    private ArrayList<Appointment> appointmentArrayList;
    private final DataService dataService = new DataService(getContext());

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private ProgressBar progressBar;

    // footer
    private String nextUrl = null, previousUrl = null;
    View tvNextUrl, tvPrevUrl;
    LinearLayout info_layout;
    ConstraintLayout.LayoutParams params;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_list, container, false);
        appointmentArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.appointentsRecycler);
        progressBar = view.findViewById(R.id.listFragmentProgressBar);

        info_layout = view.findViewById(R.id.info_layout);
        tvPrevUrl = view.findViewById(R.id.prev_page);
        tvNextUrl = view.findViewById(R.id.next_page);
        params = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();

        tvPrevUrl.setOnClickListener(button -> {
            progressBar.setVisibility(View.VISIBLE);
            getAppointmentsSubpage(previousUrl);
        });

        tvNextUrl.setOnClickListener(button -> {
            progressBar.setVisibility(View.VISIBLE);
            getAppointmentsSubpage(nextUrl);
        });
        return view;
    }

    // function called automatically after switching from SearchFragment
    public void callForAppointments(HashMap<String, String> getParams) {
        dataService.getFilteredAppointments(new DataService.FilteredAppointmentsResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), "Response with filtered appointments", Toast.LENGTH_SHORT).show();
                try {
                    nextUrl = response.getString("next");
                    previousUrl = response.getString("previous");
                    // cast to null data type if String == 'null'
                    nextUrl = nextUrl.equals("null") ? null : nextUrl;
                    previousUrl = previousUrl.equals("null") ? null : previousUrl;

                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject instance = (JSONObject) results.get(i);
                        appointmentArrayList.add(new Appointment(instance.getInt("id"), formatDateTime(instance.getString("data")), instance.getString("personel"), instance.getString("specjalnosc")));
                    }
                    // when array is done init recycler
                    initRecycler();
                    displayFooterIfNeeded();

                } catch (JSONException | ParseException exception) {
                    exception.printStackTrace();
                }
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "An error occurred while trying to connect to the server", Toast.LENGTH_LONG).show();
            }
        }, getParams);
    }

    private void getAppointmentsSubpage(String subpageURL) {
        dataService.getAppointemntsSubpage(new DataService.FilteredAppointmentsResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), "Response with filtered appointments", Toast.LENGTH_LONG).show();
                try {
                    nextUrl = response.getString("next");
                    previousUrl = response.getString("previous");
                    // cast to null datatype if String == 'null'
                    nextUrl = nextUrl.equals("null") ? null : nextUrl;
                    previousUrl = previousUrl.equals("null") ? null : previousUrl;

                    // remove old array content
                    appointmentArrayList.clear();
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject instance = (JSONObject) results.get(i);
                        appointmentArrayList.add(new Appointment(instance.getInt("id"), formatDateTime(instance.getString("data")), instance.getString("personel"), instance.getString("specjalnosc")));
                    }
                    // when array is done update recycler
                    updateRecycler();
                    displayFooterIfNeeded();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.scrollToPosition(0);
                } catch (JSONException | ParseException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "An error occurred while trying to connect to the server", Toast.LENGTH_LONG).show();
                System.out.println(message);
            }
        }, subpageURL);
    }

    private void initRecycler() {
        if (adapter == null) {
            adapter = new AppointmentAdapter(appointmentArrayList, getContext(), this);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // when done, hide progressBar
        progressBar.setVisibility(View.GONE);
    }

    private void updateRecycler() {
        adapter.notifyDataSetChanged();
    }


    public String formatDateTime(String datetime_arg) throws ParseException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            java.text.SimpleDateFormat inputDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            java.text.SimpleDateFormat outputDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            Date datetime = inputDateFormat.parse(datetime_arg);
            return outputDateFormat.format(datetime);
        }
        return datetime_arg;
    }

    // recycler on item click
    @Override
    public void onItemCLick(int position) {
        showConfirmationDialog(position);
    }

    private void showConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Czy chcesz zarezerwować tą wizytę?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do something if Yes is clicked
                        int termin_id = appointmentArrayList.get(position).getId();
                        // send POST request with user ID and appointment ID to book the appointment
                        HashMap<String, Integer> queryParams = new HashMap<>();
                        queryParams.put("uzytkownik", MainActivity.userID);
                        queryParams.put("termin", termin_id);
                        dataService.bookAppointment(queryParams, new DataService.AppointmentBookResponseListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println(response.toString());
                                Toast.makeText(getContext(), "Zarezerwowano wizytę", Toast.LENGTH_SHORT).show();
                                // on positive response, delete appointment from array & update Adapter
                                appointmentArrayList.remove(position);
                                adapter.notifyItemRemoved(position);
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(getContext(), "Serwer zwrócił błąd", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing needs to happen
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void displayFooterIfNeeded() {
        // if no other pages, set recycler height to max, hide footer
        if (nextUrl == null & previousUrl == null) {
            params.matchConstraintPercentHeight = 1;
            recyclerView.setLayoutParams(params);
            info_layout.setVisibility(View.GONE);
        }
        // if there is either next or previous page, show footer
        else if (nextUrl != null || previousUrl != null) {
            params.matchConstraintPercentHeight = 0.94f;
            recyclerView.setLayoutParams(params);
            info_layout.setVisibility(View.VISIBLE);

            if (nextUrl != null) tvNextUrl.setVisibility(View.VISIBLE);
            else tvNextUrl.setVisibility(View.GONE);

            if (previousUrl != null) tvPrevUrl.setVisibility(View.VISIBLE);
            else tvPrevUrl.setVisibility(View.GONE);
        }
    }
}