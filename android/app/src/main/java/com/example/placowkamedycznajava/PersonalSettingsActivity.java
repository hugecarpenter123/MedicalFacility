package com.example.placowkamedycznajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PersonalSettingsActivity extends AppCompatActivity {
    public int userID;
    public static final String FIRST_NAME_UPDATE_API = "first_name";
    public static final String LAST_NAME_UPDATE_API = "last_name";
    public static final String PHONE_UPDATE_API = "nr_telefonu";
    public static final String CITY_CODE_UPDATE_API = "kod_pocztowy";
    public static final String CITY_UPDATE_API = "miasto";
    public static final String STREET_UPDATE_API = "ulica";
    public static final String HOUSE_NUMBER_UPDATE_API = "nr_budynku";

    TextView firstNameLabel, lastNameLabel, phoneLabel, addressLabel;
    TextView firstNameEdit, lastNameEdit, phoneEdit, addressEdit;
    Button firstNameSubmit, lastNameSubmit, phoneSubmit, addressSubmit;
    EditText firstNameInput, lastNameInput, phoneInput, cityCodeInput, cityInput, streetInput,
            houseNumberInput;
    RelativeLayout firstNameSublayout, lastNameSublayout, phoneSublayout, addressSublayout;
    ArrayList<RelativeLayout> subFormsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);

        // get userID
        loadSharedPref();

        // prefill settings labels & inputs
        initBaseInfo();

        firstNameLabel = findViewById(R.id.first_name_edit_current);
        lastNameLabel = findViewById(R.id.last_name_edit_current);
        phoneLabel = findViewById(R.id.phone_edit_current);
        addressLabel = findViewById(R.id.address_edit_current);

        firstNameEdit = findViewById(R.id.first_name_edit_button);
        lastNameEdit = findViewById(R.id.last_name_edit_button);
        phoneEdit = findViewById(R.id.phone_edit_button);
        addressEdit = findViewById(R.id.address_edit_button);

        firstNameSubmit = findViewById(R.id.first_name_edit_submit);
        lastNameSubmit = findViewById(R.id.last_name_edit_submit);
        phoneSubmit = findViewById(R.id.phone_edit_submit);
        addressSubmit = findViewById(R.id.address_edit_submit);

        firstNameInput = findViewById(R.id.first_name_edit_input);
        lastNameInput = findViewById(R.id.last_name_edit_input);
        phoneInput = findViewById(R.id.phone_edit_input);
        cityCodeInput = findViewById(R.id.city_code_edit_input);
        cityInput = findViewById(R.id.city_edit_input);
        streetInput = findViewById(R.id.street_edit_input);
        houseNumberInput = findViewById(R.id.house_number_edit_input);

        firstNameSublayout = findViewById(R.id.first_name_edit_sublayout);
        lastNameSublayout = findViewById(R.id.last_name_edit_sublayout);
        phoneSublayout = findViewById(R.id.phone_edit_sublayout);
        addressSublayout = findViewById(R.id.address_edit_sublayout);

        // init sublayouts array
        initSubFormsArray();

        // onclick listeners for edit buttons (to show/hide edit forms)
        initEditButtonOnClicks();

        // onclick listeners for submit buttons in the sublayouts
        firstNameSubmit.setOnClickListener(button -> {
            // TODO: 02.04.2023 call DataService method to send put request & handle response
            String firstname = firstNameInput.getText().toString();
            if (firstname.isEmpty()) {
                Toast.makeText(this, "Nowe imię nie może być puste", Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put("id", String.valueOf(userID));
            putParams.put(FIRST_NAME_UPDATE_API, firstname);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, "Pomyślnie zmieniono imię", Toast.LENGTH_LONG).show();
                    firstNameLabel.setText(firstname);
                    firstNameInput.setText(firstname);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
                }
            });
        });
        lastNameSubmit.setOnClickListener(button -> {
            // TODO: 02.04.2023 call DataService method to send put request & handle response
            String lastname = lastNameInput.getText().toString();
            if (lastname.isEmpty()) {
                Toast.makeText(this, "Nowe nazwisko nie może być puste", Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put("id", String.valueOf(userID));
            putParams.put(LAST_NAME_UPDATE_API, lastname);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, "Pomyślnie zmieniono nazwisko", Toast.LENGTH_LONG).show();
                    lastNameLabel.setText(lastname);
                    lastNameInput.setText(lastname);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
                }
            });

        });
        phoneSubmit.setOnClickListener(button -> {
            // TODO: 02.04.2023 call DataService method to send put request & handle response
            String phone = phoneInput.getText().toString();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Nowe numer telefonu nie może być pusty", Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put("id", String.valueOf(userID));
            putParams.put(PHONE_UPDATE_API, phone);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, "Pomyślnie zmieniono numer telefonu", Toast.LENGTH_LONG).show();
                    phoneLabel.setText(phone);
                    phoneInput.setText(phone);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
                }
            });

        });
        addressSubmit.setOnClickListener(button -> {
            // TODO: 02.04.2023 call DataService method to send put request & handle response
            String cityCode = cityCodeInput.getText().toString();
            String city = cityInput.getText().toString();
            String street = streetInput.getText().toString();
            String houseNumber = houseNumberInput.getText().toString();

            if (cityCode.isEmpty() || city.isEmpty() || street.isEmpty() || houseNumber.isEmpty()) {
                Toast.makeText(this, "Pola z adresem nie mogą być puste", Toast.LENGTH_LONG).show();
                return;
            }

            HashMap<String, String> putParams = new HashMap<>();
            putParams.put("id", String.valueOf(userID));
            putParams.put(CITY_CODE_UPDATE_API, cityCode);
            putParams.put(CITY_UPDATE_API, city);
            putParams.put(STREET_UPDATE_API, street);
            putParams.put(HOUSE_NUMBER_UPDATE_API, houseNumber);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, "Pomyślnie zmieniono nazwisko", Toast.LENGTH_LONG).show();
                    cityCodeInput.setText(cityCode);
                    cityInput.setText(city);
                    streetInput.setText(street);
                    houseNumberInput.setText(houseNumber);
                    addressLabel.setText(String.format(Locale.getDefault(), "%s %s, %s", street, houseNumber, city));
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
                }
            });

        });

    }

    private void initSubFormsArray() {
        subFormsArr = new ArrayList<>();
        subFormsArr.add(firstNameSublayout);
        subFormsArr.add(lastNameSublayout);
        subFormsArr.add(phoneSublayout);
        subFormsArr.add(addressSublayout);
    }

    private void closeAllSubForms(RelativeLayout apartFromThis) {
        for (RelativeLayout relativeLayout : subFormsArr) {
            // close all other subform layouts apart from the one in the argument
            if (relativeLayout.getId() != apartFromThis.getId()) {
                relativeLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initEditButtonOnClicks() {
        // onclick listenerts for edit buttons (to show edit forms)
        firstNameEdit.setOnClickListener(button -> {
            if (firstNameSublayout.getVisibility() == View.VISIBLE) {
                firstNameSublayout.setVisibility(View.GONE);
            } else {
                firstNameSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(firstNameSublayout);
            }
        });
        lastNameEdit.setOnClickListener(button -> {
            if (lastNameSublayout.getVisibility() == View.VISIBLE) {
                lastNameSublayout.setVisibility(View.GONE);
            } else {
                lastNameSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(lastNameSublayout);
            }
        });
        phoneEdit.setOnClickListener(button -> {
            if (phoneSublayout.getVisibility() == View.VISIBLE) {
                phoneSublayout.setVisibility(View.GONE);
            } else {
                phoneSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(phoneSublayout);
            }
        });
        addressEdit.setOnClickListener(button -> {
            if (addressSublayout.getVisibility() == View.VISIBLE) {
                addressSublayout.setVisibility(View.GONE);
            } else {
                addressSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(addressSublayout);
            }
        });
    }

    private void loadSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        userID = sharedPreferences.getInt(LoginActivity.LOGGED_USER_ID, -1);
        if (userID == -1) {
            finish();
        }
    }

    public void initBaseInfo() {
        new DataService(this).userSettingsInfo(String.valueOf(userID), new DataService.JsonObjectResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(PersonalSettingsActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                try {
                    String firstName = response.getString(FIRST_NAME_UPDATE_API);
                    firstNameLabel.setText(firstName);
                    firstNameInput.setText(firstName);

                    String lastName = response.getString(LAST_NAME_UPDATE_API);
                    lastNameLabel.setText(lastName);
                    lastNameInput.setText(lastName);

                    String phone = response.getString(PHONE_UPDATE_API);
                    phoneLabel.setText(phone);
                    phoneInput.setText(phone);

                    String cityCode = response.getString(CITY_CODE_UPDATE_API);
                    cityCodeInput.setText(cityCode);

                    String city = response.getString(CITY_UPDATE_API);
                    cityInput.setText(city);

                    String street = response.getString(STREET_UPDATE_API);
                    streetInput.setText(street);

                    String houseNumber = response.getString(HOUSE_NUMBER_UPDATE_API);
                    houseNumberInput.setText(houseNumber);

                    addressLabel.setText(String.format(Locale.getDefault(), "%s %s, %s", street, houseNumber, city));

                } catch (JSONException e) {
                    Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd podczas przetwarzania odpowiedzi serwera", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PersonalSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
            }
        });
    }
}