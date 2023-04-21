package com.example.placowkamedycznajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import static com.example.placowkamedycznajava.utility.ApiParamNames.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placowkamedycznajava.utility.ApiParamNames;
import com.example.placowkamedycznajava.utility.ConnectionAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AccountSettingsActivity extends AppCompatActivity {
    int userID;

    TextView emailEdit, pwdEdit;
    TextView emailLabel;
    EditText emailInput, oldPwdInput, newPwdInput, newPwdRepeatInput;
    RelativeLayout emailEditSublayout, pwdEditSublayout;
    ArrayList<RelativeLayout> subFormsArr;
    Button emailSubmit, pwdSubmit;
    TextView deleteAccButton;
    final DataService dataService = new DataService(AccountSettingsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AccountsSettingsFragment onCreate()============");
        setContentView(R.layout.activity_account_settings);

        // set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
        deleteAccButton = findViewById(R.id.delete_account_btn);
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
                Toast.makeText(this, R.string.field_empty_warning, Toast.LENGTH_LONG).show();
                return;
            }

            // else create hashmap (put request parameter), make put request, deal with response
            HashMap<String, String> putParams = new HashMap<>();
            putParams.put(ID, String.valueOf(userID));
            putParams.put(EMAIL, email);

            new DataService(this).updateAccountSettings(putParams, new DataService.JsonObjectResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        String newEmail = response.getString(EMAIL);
                        emailLabel.setText(newEmail);
                        emailEditSublayout.setVisibility(View.GONE);
                        Toast.makeText(AccountSettingsActivity.this, R.string.field_update_successfull, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(AccountSettingsActivity.this, R.string.db_processing_error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String message) {
                    // TODO: 01.04.2023 change behaviour on different errors
                    Toast.makeText(AccountSettingsActivity.this, R.string.new_email_error, Toast.LENGTH_LONG).show();
                }
            });
        });

        pwdSubmit.setOnClickListener(button -> {
            String oldPwd = oldPwdInput.getText().toString();
            String newPwd = newPwdInput.getText().toString();
            String newPwdRepeat = newPwdRepeatInput.getText().toString();

            if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwdRepeat.isEmpty()) {
                Toast.makeText(AccountSettingsActivity.this, R.string.fields_empty_warning, Toast.LENGTH_LONG).show();
            } else if (!newPwd.equals(newPwdRepeat)) {
                Toast.makeText(AccountSettingsActivity.this, R.string.new_pwds_different, Toast.LENGTH_LONG).show();
            } else {
                HashMap<String, String> putParams = new HashMap<>();
                putParams.put(ID, String.valueOf(userID));
                putParams.put(UPDATE_OLD_PWD, oldPwd);
                putParams.put(UPDATE_NEW_PWD, newPwd);

                new DataService(this).updateAccountSettings(putParams, new DataService.JsonObjectResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AccountSettingsActivity.this, R.string.new_pwd_successfull, Toast.LENGTH_LONG).show();
                        pwdEditSublayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(AccountSettingsActivity.this, R.string.new_pwd_error, Toast.LENGTH_LONG).show();
                        System.out.println(message);
                    }
                });
            }
        });

        deleteAccButton.setOnClickListener((view) -> {
            // TODO: 20.04.2023 onClick ask for confirmation, then make DELETE request to endpoit, and logout
            showConfirmationDialog();
        });
    }

    private void initSubFormsArray() {
        subFormsArr = new ArrayList<>();
        subFormsArr.add(emailEditSublayout);
        subFormsArr.add(pwdEditSublayout);
    }

    private void closeAllSubForms(RelativeLayout apartFromThis) {
        for (RelativeLayout relativeLayout : subFormsArr) {
            // close all subforms
            if (relativeLayout.getId() != apartFromThis.getId()) {
                relativeLayout.setVisibility(View.GONE);
            }
        }
    }

    private void loadData() {
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
                try {
                    String email = response.getString(EMAIL);
                    emailLabel.setText(email);
                    emailInput.setText(email);
                } catch (JSONException e) {
                    Toast.makeText(AccountSettingsActivity.this, R.string.db_processing_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AccountSettingsActivity.this, R.string.db_general_error, Toast.LENGTH_LONG).show();
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

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingsActivity.this);
        builder.setMessage(R.string.delete_account_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // yes clicked ------------
                        // 1. check the connection
                        if (!ConnectionAgent.isConnected(AccountSettingsActivity.this)) {
                            Toast.makeText(AccountSettingsActivity.this, R.string.no_connection_toast, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // send DELETE request
                        dataService.deleteUserAccount(String.valueOf(MainActivity.userID), new DataService.StringResponseListener() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response.toString());
                                Toast.makeText(AccountSettingsActivity.this, R.string.delete_account_successful, Toast.LENGTH_SHORT).show();
                                sendResultBack();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(AccountSettingsActivity.this, R.string.delete_account_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing needs to happen
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendResultBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("delete", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}