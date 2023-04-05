package com.example.placowkamedycznajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.placowkamedycznajava.utility.ApiParamNames.*;
import com.example.placowkamedycznajava.utility.ConnectionAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID";

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
        changeToMainActivity();
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
            if (!ConnectionAgent.isConnected(this)) {
                Toast.makeText(LoginActivity.this, R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            DataService dataService = new DataService(this);
            dataService.logTheUser(login, password, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    // handle login response
                    try {
                        boolean isAuthenticated = response.getBoolean(BOOLEAN_LOGIN_RESPONSE);
                        if (!isAuthenticated) {
                            // handle wrong credentials...
                            Toast.makeText(LoginActivity.this, R.string.is_not_authenticated, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int id = response.getInt(ID);
                        // valid credentials log the user in...
                        saveData(id);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } catch (JSONException exception) {
                        Toast.makeText(LoginActivity.this, R.string.db_processing_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    // handle error message
                    Toast.makeText(LoginActivity.this, R.string.db_general_error, Toast.LENGTH_SHORT).show();
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
        editor.putInt(SHARED_PREF_USER_ID, data);
        editor.apply();
    }

    private void changeToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}