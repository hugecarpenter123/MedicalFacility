package com.example.placowkamedycznajava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AppointmentSearchFragment extends Fragment {

    private String[] specialityArr, personelArr, appointmentArr;
    private AutoCompleteTextView dropdownSpeciality, dropdownPersonel, dropdownAppointment;
    private ArrayAdapter<String> specialityArrayAdapter, personelArrayAdapter, appointmentArrayAdapter;

    public AppointmentSearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_appointment_search, container, false);
        dropdownPersonel = view.findViewById(R.id.autoCompletePersonel);
        initDropDownMenus(view);
        fillDropDownMenus();
        return view;
    }

    public void initDropDownMenus(View view) {
        // 1.
        dropdownSpeciality = view.findViewById(R.id.autoCompleteSpeciality);

        // 2.
        dropdownPersonel = view.findViewById(R.id.autoCompletePersonel);

        // 3.
        appointmentArr = new String[]{"Dzisiaj", "Nastpęne 10 wizyt", "Nastpęne 20 wizyt"};
        dropdownAppointment = view.findViewById(R.id.autoCompleteAppointment);
        appointmentArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, appointmentArr);
        dropdownAppointment.setAdapter(appointmentArrayAdapter);
    }

    public void fillDropDownMenus() {
        // call for base data for menus
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
                    personelArr = new String[personel.length()];
                    for (int i = 0; i < personel.length(); i++) {
                        personelArr[i] = personel.getString(i);
                    }
                    personelArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, personelArr);
                    dropdownPersonel.setAdapter(personelArrayAdapter);

                    // fill dropdown menu speciality
                    specialityArr = new String[specjalnosc.length()];
                    for (int i = 0; i < specjalnosc.length(); i++) {
                        specialityArr[i] = specjalnosc.getString(i);
                    }
                    specialityArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, specialityArr);
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
}