package com.example.placowkamedycznajava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class AccountSettingsActivity extends AppCompatActivity {
    int userID;
    public static final String EMAIL_UPDATE_API = "email";
    public static final String OLD_PWD_UPDATE_API = "old_password";
    public static final String NEW_PWD_UPDATE_API = "new_password";

    TextView emailEdit, pwdEdit;
    TextView emailLabel;
    EditText emailInput, oldPwdInput, newPwdInput, newPwdRepeatInput;
    RelativeLayout emailEditSublayout, pwdEditSublayout;
    ArrayList<RelativeLayout> subFormsArr;
    Button emailSubmit, pwdSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get userID
        loadData();

        // prefill settings labels
        initBaseInfo();

        emailEdit = findViewById(R.id.email_edit_button);
        pwdEdit = findViewById(R.id.password_edit_button);
        emailEditSublayout = findViewById(R.id.email_edit_sublayout);
        pwdEditSublayout = findViewById(R.id.password_edit_sublayout);
        emailSubmit = findViewById(R.id.email_edit_submit);
        pwdSubmit = findViewById(R.id.password_edit_submit);
        emailInput = findViewById(R.id.email_edit_input);
        oldPwdInput = findViewById(R.id.oldpassword_edit_input);
        newPwdInput = findViewById(R.id.password_edit_input);
        newPwdRepeatInput = findViewById(R.id.password_edit_repeat_input);
        emailLabel = findViewById(R.id.email_edit_current);
        initSubFormsArray();


        // on click Edit -> show update form ------------
        emailEdit.setOnClickListener(view -> {
            if (emailEditSublayout.getVisibility() == View.VISIBLE) {
                emailEditSublayout.setVisibility(View.GONE);
            } else {
                emailEditSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(emailEditSublayout);
            }
        });

        pwdEdit.setOnClickListener(view -> {
            if (pwdEditSublayout.getVisibility() == View.VISIBLE) {
                pwdEditSublayout.setVisibility(View.GONE);
            } else {
                pwdEditSublayout.setVisibility(View.VISIBLE);
                closeAllSubForms(pwdEditSublayout);
            }
        });
        // -----------------------------------------------

        // onClick submit send request to database and update the user's data
        emailSubmit.setOnClickListener(button -> {
            String email = emailInput.getText().toString();
            // if email is empty, show message don't process further
            if (email.isEmpty()) {
                Toast.makeText(this, "Nowy email nie może być pusty", Toast.LENGTH_LONG).show();
                return;
            }

            // else create hashmap (put request parameter), make put request, deal with response
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put("id", String.valueOf(userID));
            putParams.put(EMAIL_UPDATE_API, email);

            new DataService(this)
                    .updateAccountSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    Toast.makeText(AccountSettingsActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    try {
                        String newEmail = response.getString(EMAIL_UPDATE_API);
                        emailLabel.setText(newEmail);
                        emailEditSublayout.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this, "Pomyślnie zmieniono email", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(AccountSettingsActivity.this, "Wydarzył się błąd podczas przetwarzania odpowiedzi serwera", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String message) {
                    // TODO: 01.04.2023 change behaviour on different errors
                    Toast.makeText(AccountSettingsActivity.this, "Email nie poprawny, być może jest zajęty", Toast.LENGTH_LONG).show();
                }
            });
        });

        pwdSubmit.setOnClickListener(button -> {
            String oldPwd = oldPwdInput.getText().toString();
            String newPwd = newPwdInput.getText().toString();
            String newPwdRepeat = newPwdRepeatInput.getText().toString();

            if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwdRepeat.isEmpty()) {
                Toast.makeText(AccountSettingsActivity.this, "Pola z hasłami muszą być wypełnione", Toast.LENGTH_LONG).show();
            } else if (!newPwd.equals(newPwdRepeat)) {
                Toast.makeText(AccountSettingsActivity.this, "Nowe hasła są różne", Toast.LENGTH_LONG).show();
            } else {
                HashMap<String, String> putParams = new HashMap<>();
                putParams.put("id", String.valueOf(userID));
                putParams.put(OLD_PWD_UPDATE_API, oldPwd);
                putParams.put(NEW_PWD_UPDATE_API, newPwd);

                new DataService(this).updateAccountSettings(putParams, new DataService.JsonObjectResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AccountSettingsActivity.this, "Pomyślnie zmieniono hasło", Toast.LENGTH_LONG).show();
                        pwdEditSublayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(AccountSettingsActivity.this, "Wydarzył się błąd, upewnij się, że wprowadzasz poprawne hasło", Toast.LENGTH_LONG).show();
                        System.out.println(message);
                    }
                });
            }
        });
    }

    private void initSubFormsArray() {
        subFormsArr = new ArrayList<>();
        subFormsArr.add(emailEditSublayout);
        subFormsArr.add(pwdEditSublayout);
    }

    private void closeAllSubForms(RelativeLayout apartFromThis) {
        for (RelativeLayout relativeLayout : subFormsArr) {
            // close (set v.GONE) all sub-forms
            if (relativeLayout.getId() != apartFromThis.getId()) {
                relativeLayout.setVisibility(View.GONE);
            }
        }
    }

    private void loadData() {
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
                Toast.makeText(AccountSettingsActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                try {
                    String email = response.getString(EMAIL_UPDATE_API);
                    emailLabel.setText(email);
                    emailInput.setText(email);
                } catch (JSONException e) {
                    Toast.makeText(AccountSettingsActivity.this, "Wydarzył się błąd podczas przetwarzania odpowiedzi serwera", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AccountSettingsActivity.this, "Wydarzył się błąd, podczas łączenia się z serwerem", Toast.LENGTH_LONG).show();
            }
        });
    }
}