package com.example.placowkamedycznajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.fonts.SystemFonts;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGGED_USER_ID = "SHARED_PREF_USER_ID";

    // app elements
    EditText loginInput;
    EditText passwordInput;
    Button loginButton;
    TextView tvRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: 18.03.2023 remove later --------
//        changeToMainActivity();
        // ======================================

        loginInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        tvRedirect = findViewById(R.id.redirect_to_register);

        loginButton.setOnClickListener(button -> {
            String login = loginInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (!validateInputs()) {
                // do something...
                return;
            }
            DataService dataService = new DataService(this);
            dataService.logTheUser(login, password, new DataService.LoginResponseListener() {
                @Override
                public void onLoginResponse(JSONObject response) {
                    // handle login response
                    try {
                        boolean isAuthenticated = response.getBoolean("isAuthenticated");
                        if (!isAuthenticated) {
                            // handle wrong credentials...
                            Toast.makeText(LoginActivity.this, "Użytkownik nie istnieje", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int id = response.getInt("id");
                        Toast.makeText(LoginActivity.this, "Użytkownik potwierdzony, id: " + id, Toast.LENGTH_SHORT).show();
                        // valid credentials log the user in...
                        saveData(id);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } catch (JSONException exception) {
                        Toast.makeText(LoginActivity.this, "Podczas otrzymywania danych wydarzył się błaąd", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    // handle error message
                    Toast.makeText(LoginActivity.this, "Błąd podczas próby połączenia się z bazą danych", Toast.LENGTH_SHORT).show();
                    System.out.println(message);
                }
            });
        });

        // redirect the user when clicks on redirect View
        tvRedirect = findViewById(R.id.redirect_to_register);
        tvRedirect.setOnClickListener(view -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });
    }

    private boolean validateInputs() {
        // validation...
        return true;
    }

    private void saveData(int data) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOGGED_USER_ID, data);
        editor.apply();
    }

    private void changeToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}