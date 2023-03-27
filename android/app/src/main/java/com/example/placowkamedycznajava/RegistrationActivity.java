package com.example.placowkamedycznajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    public static final String USERNAME_REGISTER_API = "username";
    public static final String FIRST_NAME_REGISTER_API = "first_name";
    public static final String LAST_NAME_REGISTER_API = "last_name";
    public static final String EMAIL_REGISTER_API = "email";
    public static final String PASSWORD_REGISTER_API = "password";
    public static final String PESEL_REGISTER_API = "pesel";
    public static final String PHONE_REGISTER_API = "nr_telefonu";
    public static final String CITY_REGISTER_API = "miasto";
    public static final String CITY_CODE_REGISTER_API = "kod_pocztowy";
    public static final String STREET_REGISTER_API = "ulica";
    public static final String HOUSE_NUMBER_REGISTER_API = "nr_budynku";

    TextView tvRedirectLogin;
    EditText usernameInput, emailInput, pwdInput1, pwdInput2, firstNameInput, surnameInput,
            peselInput, cityInput, cityCodeInput, streetInput, houseNumberInput, phoneInput;
    Button registerButon;
    HashMap<String, String> registerParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // redirect to login on click -------------------------
        tvRedirectLogin = findViewById(R.id.redirect_to_login);
        tvRedirectLogin.setOnClickListener(view -> {
            switchToLoginActivity();
        });

        usernameInput = findViewById(R.id.username_register);
        emailInput = findViewById(R.id.email_register);
        pwdInput1 = findViewById(R.id.pwd1_register);
        pwdInput2 = findViewById(R.id.pwd2_register);
        firstNameInput = findViewById(R.id.first_name_register);
        surnameInput = findViewById(R.id.surname_register);
        peselInput = findViewById(R.id.pesel_register);
        cityInput = findViewById(R.id.city_register);
        cityCodeInput = findViewById(R.id.city_code_register);
        streetInput = findViewById(R.id.street_register);
        houseNumberInput = findViewById(R.id.house_number_register);
        phoneInput = findViewById(R.id.phone_register);

        registerParams = new HashMap<>();

        registerButon = findViewById(R.id.register_button);
        registerButon.setOnClickListener(button -> {
            if (validateInputs()) {
                registerTheUser();
            }
        });
    }

    private void switchToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean validateInputs() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pwd1 = pwdInput1.getText().toString();
        String pwd2 = pwdInput2.getText().toString();
        String firstName = firstNameInput.getText().toString().trim();
        String surname = usernameInput.getText().toString().trim();
        String pesel = peselInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String cityCode = cityCodeInput.getText().toString().trim();
        String street = streetInput.getText().toString().trim();
        String houseNumber = houseNumberInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // perform validation here...
        boolean SOMETHING_IS_WRONG = false;
        if (SOMETHING_IS_WRONG) {
            return false;
        }

        // if all good, fill the registerParams:
        registerParams.put(USERNAME_REGISTER_API, username);
        registerParams.put(EMAIL_REGISTER_API, email);
        registerParams.put(PASSWORD_REGISTER_API, pwd1);
        registerParams.put(FIRST_NAME_REGISTER_API, firstName);
        registerParams.put(LAST_NAME_REGISTER_API, surname);
        registerParams.put(PESEL_REGISTER_API, pesel);
        registerParams.put(PHONE_REGISTER_API, phone);
        registerParams.put(CITY_REGISTER_API, city);
        registerParams.put(CITY_CODE_REGISTER_API, cityCode);
        registerParams.put(STREET_REGISTER_API, street);
        registerParams.put(HOUSE_NUMBER_REGISTER_API, houseNumber);
        return true;
    }

    private void registerTheUser() {
        DataService dataService = new DataService(this);
        dataService.registerTheUser(registerParams, new DataService.RegisterResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(RegistrationActivity.this, "Zarejestrowano pomyślnie", Toast.LENGTH_LONG).show();
                redirectToLoginActivity();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegistrationActivity.this, "Error happened during communication with server", Toast.LENGTH_LONG).show();
                System.out.println(message);
            }
        });
    }

    private void redirectToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}