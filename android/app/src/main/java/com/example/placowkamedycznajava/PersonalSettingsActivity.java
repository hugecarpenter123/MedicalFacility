package com.example.placowkamedycznajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.placowkamedycznajava.utility.ApiParamNames.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import java.util.Objects;

public class PersonalSettingsActivity extends AppCompatActivity {
    public int userID;

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

        // set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
            String firstname = firstNameInput.getText().toString();
            if (firstname.isEmpty()) {
                Toast.makeText(this, R.string.field_empty_warning, Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put(ID, String.valueOf(userID));
            putParams.put(FIRST_NAME, firstname);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.field_update_successfull, Toast.LENGTH_LONG).show();
                    firstNameLabel.setText(firstname);
                    firstNameInput.setText(firstname);
                    firstNameSublayout.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
                }
            });
        });
        lastNameSubmit.setOnClickListener(button -> {
            String lastname = lastNameInput.getText().toString();
            if (lastname.isEmpty()) {
                Toast.makeText(this, R.string.field_empty_warning, Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put(ID, String.valueOf(userID));
            putParams.put(LAST_NAME, lastname);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.field_update_successfull, Toast.LENGTH_LONG).show();
                    lastNameLabel.setText(lastname);
                    lastNameInput.setText(lastname);
                    lastNameSublayout.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
                }
            });

        });
        phoneSubmit.setOnClickListener(button -> {
            String phone = phoneInput.getText().toString();
            if (phone.isEmpty()) {
                Toast.makeText(this, R.string.field_empty_warning, Toast.LENGTH_LONG).show();
                return;
            }
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put(ID, String.valueOf(userID));
            putParams.put(PHONE, phone);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.field_update_successfull, Toast.LENGTH_LONG).show();
                    phoneLabel.setText(phone);
                    phoneInput.setText(phone);
                    phoneSublayout.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
                }
            });

        });
        addressSubmit.setOnClickListener(button -> {
            String cityCode = cityCodeInput.getText().toString();
            String city = cityInput.getText().toString();
            String street = streetInput.getText().toString();
            String houseNumber = houseNumberInput.getText().toString();

            if (cityCode.isEmpty() || city.isEmpty() || street.isEmpty() || houseNumber.isEmpty()) {
                Toast.makeText(this, R.string.fields_empty_warning, Toast.LENGTH_LONG).show();
                return;
            }

            HashMap<String, String> putParams = new HashMap<>();
            putParams.put(ID, String.valueOf(userID));
            putParams.put(CITY_CODE, cityCode);
            putParams.put(CITY, city);
            putParams.put(STREET, street);
            putParams.put(HOUSE_NUMBER, houseNumber);
            new DataService(this).updatePersonalSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.field_update_successfull, Toast.LENGTH_LONG).show();
                    cityCodeInput.setText(cityCode);
                    cityInput.setText(city);
                    streetInput.setText(street);
                    houseNumberInput.setText(houseNumber);
                    addressLabel.setText(String.format(Locale.getDefault(), "%s %s, %s", street, houseNumber, city));
                    addressSublayout.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
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
        userID = sharedPreferences.getInt(LoginActivity.SHARED_PREF_USER_ID, -1);
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
                    String firstName = response.getString(FIRST_NAME);
                    firstNameLabel.setText(firstName);
                    firstNameInput.setText(firstName);

                    String lastName = response.getString(LAST_NAME);
                    lastNameLabel.setText(lastName);
                    lastNameInput.setText(lastName);

                    String phone = response.getString(PHONE);
                    phoneLabel.setText(phone);
                    phoneInput.setText(phone);

                    String cityCode = response.getString(CITY_CODE);
                    cityCodeInput.setText(cityCode);

                    String city = response.getString(CITY);
                    cityInput.setText(city);

                    String street = response.getString(STREET);
                    streetInput.setText(street);

                    String houseNumber = response.getString(HOUSE_NUMBER);
                    houseNumberInput.setText(houseNumber);

                    addressLabel.setText(String.format(Locale.getDefault(), "%s %s, %s", street, houseNumber, city));

                } catch (JSONException e) {
                    Toast.makeText(PersonalSettingsActivity.this, R.string.db_processing_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PersonalSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}