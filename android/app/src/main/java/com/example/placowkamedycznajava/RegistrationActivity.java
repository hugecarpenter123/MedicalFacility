package com.example.placowkamedycznajava;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.placowkamedycznajava.utility.ApiParamNames.*;
import com.example.placowkamedycznajava.utility.ConnectionAgent;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
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
            if (!ConnectionAgent.isConnected(this)) {
                Toast.makeText(RegistrationActivity.this, R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                return;
            }
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
        String surname = surnameInput.getText().toString().trim();
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
        registerParams.put(USERNAME, username);
        registerParams.put(EMAIL, email);
        registerParams.put(PASSWORD, pwd1);
        registerParams.put(FIRST_NAME, firstName);
        registerParams.put(LAST_NAME, surname);
        registerParams.put(PESEL, pesel);
        registerParams.put(PHONE, phone);
        registerParams.put(CITY, city);
        registerParams.put(CITY_CODE, cityCode);
        registerParams.put(STREET, street);
        registerParams.put(HOUSE_NUMBER, houseNumber);
        return true;
    }

    private void registerTheUser() {
        DataService dataService = new DataService(this);
        dataService.registerTheUser(registerParams, new DataService.StringResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(RegistrationActivity.this, R.string.register_successfull, Toast.LENGTH_LONG).show();
                redirectToLoginActivity();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegistrationActivity.this, R.string.db_error_register, Toast.LENGTH_LONG).show();
                System.out.println(message);
            }
        });
    }

    private void redirectToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}