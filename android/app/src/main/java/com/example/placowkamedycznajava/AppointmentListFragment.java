package com.example.placowkamedycznajava;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.placowkamedycznajava.utility.ApiParamNames;
import com.example.placowkamedycznajava.utility.ConnectionAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AppointmentListFragment extends Fragment implements AppointmentAdapter.OnClickListener {
    // data for refresh action
    private HashMap<String, String> getParamsCopy;
    private String currentSubpage = null;
    SwipeRefreshLayout refreshLayout;

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

        refreshLayout = view.findViewById(R.id.search_list_refresh_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performRefresh();
            }
        });

        // make request for filtered appointments with search parameters sent from previous Fragment
        getAppointments(getParamsCopy);

        return view;
    }


    public void callForAppointments(HashMap<String, String> getParams) {
        // save hashmap with parameters
        getParamsCopy = getParams;
    }

    private void getAppointments(HashMap<String, String> getParams) {
        // 1. check connection
        if (!ConnectionAgent.isConnected(requireContext())) {
            Toast.makeText(getContext(), R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
            return;
        }
        // 2. get Appointments from database and populate recycler
        dataService.getFilteredAppointments(new DataService.JsonObjectResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getContext(), "Response with filtered appointments", Toast.LENGTH_SHORT).show();
                try {
                    nextUrl = response.getString(ApiParamNames.APPOINTMENTS_NEXT_PAGE);
                    previousUrl = response.getString(ApiParamNames.APPOINTMENTS_PREVIOUS_PAGE);
                    // cast to null data type if String == 'null'
                    nextUrl = nextUrl.equals("null") ? null : nextUrl;
                    previousUrl = previousUrl.equals("null") ? null : previousUrl;

                    appointmentArrayList.clear();
                    JSONArray results = response.getJSONArray(ApiParamNames.APPOINTMENTS_RESULT_ARRAY);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject instance = (JSONObject) results.get(i);
                        appointmentArrayList.add(new Appointment(
                                instance.getInt(ApiParamNames.ID),
                                formatDateTime(instance.getString(ApiParamNames.APPOINTMENTS_DATE)),
                                instance.getString(ApiParamNames.APPOINTMENTS_PERSONEL),
                                instance.getString(ApiParamNames.APPOINTMENTS_SPECIALITY))
                        );
                    }
                    // when array is done init recycler
                    initRecycler();
                    displayFooterIfNeeded();

                } catch (JSONException | ParseException exception) {
                    exception.printStackTrace();
                    // shouldn't happen at all, if does then developers fault
                    Toast.makeText(getContext(), R.string.db_processing_error, Toast.LENGTH_SHORT).show();
                }

                // stop refreshing animation
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), R.string.db_general_error, Toast.LENGTH_LONG).show();
                refreshLayout.setRefreshing(false);
            }
        }, getParams);
    }

    private void getAppointmentsSubpage(String subpageURL) {
        // update global variable holding current subpage
        currentSubpage = subpageURL;

        dataService.getAppointemntsSubpage(new DataService.JsonObjectResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nextUrl = response.getString(ApiParamNames.APPOINTMENTS_NEXT_PAGE);
                    previousUrl = response.getString(ApiParamNames.APPOINTMENTS_PREVIOUS_PAGE);
                    // cast to null datatype if String == 'null'
                    nextUrl = nextUrl.equals("null") ? null : nextUrl;
                    previousUrl = previousUrl.equals("null") ? null : previousUrl;

                    // remove old array content
                    appointmentArrayList.clear();
                    JSONArray results = response.getJSONArray(ApiParamNames.APPOINTMENTS_RESULT_ARRAY);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject instance = (JSONObject) results.get(i);
                        appointmentArrayList.add(new Appointment(
                                instance.getInt(ApiParamNames.ID),
                                formatDateTime(instance.getString(ApiParamNames.APPOINTMENTS_DATE)),
                                instance.getString(ApiParamNames.APPOINTMENTS_PERSONEL),
                                instance.getString(ApiParamNames.APPOINTMENTS_SPECIALITY))
                        );
                    }
                    // when array is done update recycler
                    updateRecycler();
                    displayFooterIfNeeded();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.scrollToPosition(0);

                } catch (JSONException | ParseException exception) {
                    exception.printStackTrace();
                }

                // stop refreshing animation
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), R.string.db_general_error, Toast.LENGTH_LONG).show();
                System.out.println(message);
                refreshLayout.setRefreshing(false);
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
        builder.setMessage(R.string.booking_confirmation)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // yes clicked ------------

                        // 1. check the connection
                        if (!ConnectionAgent.isConnected(requireContext())) {
                            Toast.makeText(getContext(), R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 2. send POST request with user ID and appointment ID to book the appointment
                        int termin_id = appointmentArrayList.get(position).getId();
                        HashMap<String, Integer> queryParams = new HashMap<>();
                        queryParams.put(ApiParamNames.BOOK_USER_ID, MainActivity.userID);
                        queryParams.put(ApiParamNames.BOOK_APPOINTMENT_ID, termin_id);
                        dataService.bookAppointment(queryParams, new DataService.JsonObjectResponseListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println(response.toString());
                                Toast.makeText(getContext(), R.string.booking_successfull, Toast.LENGTH_SHORT).show();
                                // on positive response, delete appointment from array & update Adapter
                                appointmentArrayList.remove(position);
                                adapter.notifyItemRemoved(position);
                            }

                            @Override
                            public void onError(String message) {
                                // if error happens (which shouldn't if there is connection) ->
                                // trigger refresh, this appointment can be non-existent now
                                Toast.makeText(getContext(), R.string.booking_error, Toast.LENGTH_SHORT).show();
                                refreshLayout.setRefreshing(true);
                                performRefresh();
                            }
                        });
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

    private void performRefresh() {
        if (currentSubpage != null) {
            getAppointmentsSubpage(currentSubpage);
        } else {
            getAppointments(getParamsCopy);
        }
    }
}