package com.example.placowkamedycznajava;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.placowkamedycznajava.utility.HttpRequestSingleton;

import static com.example.placowkamedycznajava.utility.ApiParamNames.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    public static final String LOGIN_URL = "http://192.168.1.39:8000/api/uzytkownik/zaloguj/";
    public static final String INFO_URL = "http://192.168.1.39:8000/api/info/";
    public static final String APPOINTMENTS_URL = "http://192.168.1.39:8000/api/termin";
    public static final String BOOK_APPOINTMENT_URL = "http://192.168.1.39:8000/api/wizyta/";
    public static final String REGISTER_URL = "http://192.168.1.39:8000/api/uzytkownik/";
    public static final String USER_INFO_URL = "http://192.168.1.39:8000/api/uzytkownik/";
    public static final String BOOKED_APPOINTMENTS_URL = "http://192.168.1.39:8000/api/wizyta/";
    public static final String USER_ACCOUNT_UPDATE_URL = "http://192.168.1.39:8000/api/uzytkownik/konto/";
    public static final String USER_ACCOUNT_INFO_URL = "http://192.168.1.39:8000/api/uzytkownik/konto/"; // + `id/`
    Context context;

    public DataService(Context context) {
        this.context = context;
    }

    interface JsonObjectResponseListener {
        void onResponse(JSONObject response);

        void onError(String error);
    }

    interface StringResponseListener {
        void onResponse(String response);

        void onError(String error);
    }

    public void logTheUser(String login, String password, JsonObjectResponseListener responseListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(LOGIN, login);
            postData.put(PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // handle valid response
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // handle invalid response
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(postRequest);
    }

    public void getBaseInfo(JsonObjectResponseListener responseListener) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, INFO_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }

    public void getFilteredAppointments(JsonObjectResponseListener responseListener, HashMap<String, String> queryParams) {
        Uri.Builder builder = Uri.parse(APPOINTMENTS_URL).buildUpon();
        if (queryParams.containsKey(SEARCH_PERSONEL_ID))
            builder.appendQueryParameter(SEARCH_PERSONEL_ID, queryParams.get(SEARCH_PERSONEL_ID));
        if (queryParams.containsKey(SEARCH_DATE))
            builder.appendQueryParameter(SEARCH_DATE, queryParams.get(SEARCH_DATE));
        if (queryParams.containsKey(SEARCH_SPECIALITY_ID))
            builder.appendQueryParameter(SEARCH_SPECIALITY_ID, queryParams.get(SEARCH_SPECIALITY_ID));
        System.out.println("Final url to get data from: " + builder.toString());
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }

    public void getAppointemntsSubpage(JsonObjectResponseListener responseListener, String subpageUrl) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, subpageUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }

    public void bookAppointment(HashMap<String, Integer> queryParams, JsonObjectResponseListener responseListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(BOOK_APPOINTMENT_ID, queryParams.get(BOOK_APPOINTMENT_ID));
            postData.put(BOOK_USER_ID, queryParams.get(BOOK_USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, BOOK_APPOINTMENT_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(postRequest);
    }

    public void registerTheUser(HashMap<String, String> queryParams, StringResponseListener responseListener) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError(error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                return queryParams;
            }
        };
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(postRequest);
    }

    public void getUserAppointments(String userID, JsonObjectResponseListener responseListener) {
        String URL = USER_INFO_URL + userID + '/';
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }

    public void deleteUserAppointment(String appointmentID, StringResponseListener responseListener) {
        final int[] statusCode = new int[1];
        String URL = BOOKED_APPOINTMENTS_URL + appointmentID + '/';
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("status code from DataService: " + statusCode[0]);
                responseListener.onResponse(String.valueOf(statusCode[0]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError(error.toString());
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(deleteRequest);
    }

    public void updateAccountSettings(HashMap<String, String> putParams, JsonObjectResponseListener responseListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(ID, putParams.get(ID));
            postData.put(EMAIL, putParams.get(EMAIL));
            postData.put(UPDATE_OLD_PWD, putParams.get(UPDATE_OLD_PWD));
            postData.put(UPDATE_NEW_PWD, putParams.get(UPDATE_NEW_PWD));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, USER_ACCOUNT_UPDATE_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(putRequest);
    }

    public void updatePersonalSettings(HashMap<String, String> putParams, JsonObjectResponseListener responseListener) {
        JSONObject putData = new JSONObject();
        try {
            putData.put(ID, putParams.get(ID));
            putData.put(FIRST_NAME, putParams.get(FIRST_NAME));
            putData.put(LAST_NAME, putParams.get(LAST_NAME));
            putData.put(PHONE, putParams.get(PHONE));
            putData.put(CITY_CODE, putParams.get(CITY_CODE));
            putData.put(CITY, putParams.get(CITY));
            putData.put(STREET, putParams.get(STREET));
            putData.put(HOUSE_NUMBER, putParams.get(HOUSE_NUMBER));
            System.out.println(putData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, USER_ACCOUNT_UPDATE_URL, putData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(putRequest);
    }

    // used in both Account/Personal-SettingsActivity to display current user's data
    public void userSettingsInfo(String id, JsonObjectResponseListener responseListener) {
        String URL = USER_ACCOUNT_INFO_URL + id + "/";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError(error.toString());
            }
        });

        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }
}
