package com.example.placowkamedycznajava;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataService {
    public static final String LOGIN_URL = "http://192.168.1.39:8000/api/uzytkownik/zaloguj/";
    Context context;

    public DataService(Context context) {
        this.context = context;
    }

    interface LoginResponseListener {
        void onLoginResponse(JSONObject response);
        void onError(String message);
    }

    public void logTheUser(String login, String password, LoginResponseListener responseListener) {
        System.out.println("messagePostRequest called() ----------------");

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", login);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, postData ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject  response) {
                System.out.println("onResponse called() ----------------");
                System.out.println(response);
                // handle valid response
                responseListener.onLoginResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error occured during posting the data", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                responseListener.onError(error.toString());
            }

        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(postRequest);
    }


}