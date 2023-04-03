package com.example.placowkamedycznajava;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AppointmentSearchFragment extends Fragment {

    private String[] appointmentArr;
    Map<String, String> appointmentDict;
    private String[] specialityArr, personelArr;
    private int[] specialityIdArr, personelIdArr, appointmentIdArr;
    private AutoCompleteTextView dropdownSpeciality, dropdownPersonel, dropdownAppointment;
    private ArrayAdapter<String> specialityArrayAdapter, personelArrayAdapter, appointmentArrayAdapter;
    private String personelQ, appointmentQ, specialityQ;
    private Button searchButton;

    // DatePicker -----------
    private DatesQueryHelper datesQueryHelper;
    private DatePickerDialog datePickerDialog;
    private Button datePickerButton;

    // listener of onSearchButton click
    AppointmentSearchFragmentListener listener;

    public AppointmentSearchFragment() {
        // Required empty public constructor
    }

    interface AppointmentSearchFragmentListener {
        void onSearchClick(HashMap<String, String> getParams);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_search, container, false);

        // init date helper & datePicker
        datesQueryHelper = new DatesQueryHelper();
        datePickerButton = view.findViewById(R.id.date_picker_button);
        initDatePicker();
        datePickerButton.setOnClickListener(button -> {
            openDatePicker();
        });

        // init dropdown menus
        initDropDownMenus(view);
        fillDropDownMenus();

        // set onMenuItemClickListeners and handle choices
        dropdownPersonel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // last position indicates default (null)
                if (position == personelArr.length - 1) {
                    personelQ = null;
                    System.out.println("Choosen value: " + personelArr[position]);
                }
                else {
                    // save in the "spersonelQ" db id of personel
                    personelQ = String.valueOf(personelIdArr[position]);
                }
            }
        });

        dropdownSpeciality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == specialityArr.length - 1) {
                    System.out.println("specialityQ casted to null");
                    specialityQ = null;
                } else {
                    // save in the "specialityQ" db id of speciality
                    specialityQ = String.valueOf(personelIdArr[position]);
                }
            }
        });

        dropdownAppointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (appointmentArr.length - 1 == position) {
                    // show datePickerButton and set appointmentQ to its visible value
                    datePickerButton.setVisibility(View.VISIBLE);
                    appointmentQ = datePickerButton.getText().toString();
                } else {
                    datePickerButton.setVisibility(View.GONE);
                    appointmentQ = datesQueryHelper.getDateQueryDict().get(appointmentArr[position]);
                }
            }
        });

        // set searchButton onClickListener
        searchButton = view.findViewById(R.id.search_appointments_btn);
        searchButton.setOnClickListener(button -> {
            HashMap<String, String> getParams = new HashMap<>();
            if (personelQ != null) getParams.put("personel_id", personelQ);
            if (appointmentQ != null) getParams.put("data", appointmentQ);
            if (specialityQ != null) getParams.put("specjalnosc_id", specialityQ);

            System.out.println("getParams for search: " + getParams);
            // this listener must be implemented by MainActivity
            listener.onSearchClick(getParams);
        });

        return view;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = String.format("%s-%s-%s", dayOfMonth, month, year);
                datePickerButton.setText(date);
                appointmentQ = date;
            }
        };
        int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener ,year, month, day);
        datePickerButton.setText(datesQueryHelper.todayDateAsString());
    }

    public void openDatePicker() {
        datePickerDialog.show();
    }

    public void initDropDownMenus(View view) {
        // 1.
        dropdownSpeciality = view.findViewById(R.id.autoCompleteSpeciality);
        // 2.
        dropdownPersonel = view.findViewById(R.id.autoCompletePersonel);
        // 3.
        appointmentArr = datesQueryHelper.getAppointmentArr();
        dropdownAppointment = view.findViewById(R.id.autoCompleteAppointment);
        appointmentArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, appointmentArr);
        dropdownAppointment.setAdapter(appointmentArrayAdapter);
    }

    public void fillDropDownMenus() {
        // get request for base data for menus
        DataService dataService = new DataService(getContext());
        dataService.getBaseInfo(new DataService.BaseInfoResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(), "Response from Fragment", Toast.LENGTH_SHORT).show();
                try {
                    // retrieve data
                    JSONArray specjalnosc = response.getJSONArray("specjalnosc");
                    JSONArray personel = response.getJSONArray("personel");

                    // fill dropdown menu personel
                    personelArr = new String[personel.length() + 1];
                    personelIdArr = new int[personel.length()];
                    for (int i = 0; i < personel.length(); i++) {
                        JSONArray id_data = personel.getJSONArray(i);
                        personelIdArr[i] = id_data.getInt(0);
                        personelArr[i] = id_data.getString(1);
                    }
                    // add choicless choice
                    personelArr[personel.length()] =  getResources().getString(R.string.personel_all_choice);
                    personelArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, personelArr);
                    dropdownPersonel.setAdapter(personelArrayAdapter);

                    // fill dropdown menu speciality
                    specialityArr = new String[specjalnosc.length() + 1];
                    specialityIdArr = new int[specjalnosc.length()];
                    for (int i = 0; i < specjalnosc.length(); i++) {
                        JSONArray id_data = specjalnosc.getJSONArray(i);
                        specialityIdArr[i] = id_data.getInt(0);
                        specialityArr[i] = id_data.getString(1);
                    }
                    // add choicless choice
                    specialityArr[specjalnosc.length()] =  getResources().getString(R.string.speciality_all_choice);
                    specialityArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, specialityArr);
                    dropdownSpeciality.setAdapter(specialityArrayAdapter);

                } catch (JSONException exception) {
                    exception.printStackTrace();
                    Toast.makeText(getContext(), "Błąd podczas pobierania danych z serwera", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Błąd podczas próby połączenia się z serwerem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentSearchFragmentListener) {
            listener = (AppointmentSearchFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + ": must implement AppointmentSearchFragmentListener");
        }
    }
}