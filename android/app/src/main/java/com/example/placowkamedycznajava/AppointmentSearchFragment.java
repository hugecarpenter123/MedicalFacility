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

import com.example.placowkamedycznajava.utility.ApiParamNames;
import com.example.placowkamedycznajava.utility.ConnectionAgent;
import com.example.placowkamedycznajava.utility.DatesQueryHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
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
                // last position is `choiceless` so set to null
                if (position == personelArr.length - 1) {
                    personelQ = null;
                }
                else {
                    // save in the "spersonelQ" id of personel
                    personelQ = String.valueOf(personelIdArr[position]);
                }
            }
        });

        dropdownSpeciality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == specialityArr.length - 1) {
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
                    appointmentQ = DatesQueryHelper.dateQueryDict.get(appointmentArr[position]);
                }
            }
        });

        // set searchButton onClickListener
        searchButton = view.findViewById(R.id.search_appointments_btn);
        searchButton.setOnClickListener(button -> {

            if (!ConnectionAgent.isConnected(requireContext())) {
                Toast.makeText(getContext(), R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> getParams = new HashMap<>();
            if (personelQ != null) getParams.put(ApiParamNames.SEARCH_PERSONEL_ID, personelQ);
            if (appointmentQ != null) getParams.put(ApiParamNames.SEARCH_DATE, appointmentQ);
            if (specialityQ != null) getParams.put(ApiParamNames.SEARCH_SPECIALITY_ID, specialityQ);

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
        datePickerButton.setText(DatesQueryHelper.todayDateAsString());
    }

    public void openDatePicker() {
        datePickerDialog.show();
    }

    public void initDropDownMenus(View view) {
        dropdownSpeciality = view.findViewById(R.id.autoCompleteSpeciality);
        dropdownPersonel = view.findViewById(R.id.autoCompletePersonel);
        appointmentArr = DatesQueryHelper.appointmentArr;
        dropdownAppointment = view.findViewById(R.id.autoCompleteAppointment);
        appointmentArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, appointmentArr);
        dropdownAppointment.setAdapter(appointmentArrayAdapter);
    }

    public void fillDropDownMenus() {
        // get request for base data for menus
        DataService dataService = new DataService(getContext());
        dataService.getBaseInfo(new DataService.JsonObjectResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray specjalnosc = response.getJSONArray(ApiParamNames.SPECIALITY_ARRAY);
                    JSONArray personel = response.getJSONArray(ApiParamNames.PERSONEL_ARRAY);

                    // fill dropdown menu personel
                    personelArr = new String[personel.length() + 1];
                    personelIdArr = new int[personel.length()];
                    for (int i = 0; i < personel.length(); i++) {
                        JSONArray id_data = personel.getJSONArray(i);
                        personelIdArr[i] = id_data.getInt(0);
                        personelArr[i] = id_data.getString(1);
                    }
                    // add choiceless choice
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
                    // add choiceless choice
                    specialityArr[specjalnosc.length()] =  getResources().getString(R.string.speciality_all_choice);
                    specialityArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, specialityArr);
                    dropdownSpeciality.setAdapter(specialityArrayAdapter);

                } catch (JSONException exception) {
                    exception.printStackTrace();
                    // shouldn't happen in production
                    Toast.makeText(getContext(), R.string.db_processing_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), R.string.db_general_error, Toast.LENGTH_SHORT).show();
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