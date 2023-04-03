package com.example.placowkamedycznajava;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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

    // TODO: 01.04.2023 remove all other interfaces, leave universal ones
    interface JsonObjectResponseListener {
        void onResponse(JSONObject response);

        void onError(String error);
    }

    interface StringResponseListener {
        void onResponse(String response);

        void onError(String error);
    }
    // =======================================================================

    // for login Activity
    interface LoginResponseListener {
        void onLoginResponse(JSONObject response);

        void onError(String message);
    }

    // for AppointmentSearchFragment
    interface BaseInfoResponseListener {
        void onResponse(JSONObject response);

        void onError(String message);
    }

    // for AppointmentListFragment
    interface FilteredAppointmentsResponseListener {
        void onResponse(JSONObject response);

        void onError(String message);
    }

    // for AppointmentListFragment
    interface AppointmentBookResponseListener {
        void onResponse(JSONObject response);

        void onError(String message);
    }

    // for RegisterActivity
    interface RegisterResponseListener {
        void onResponse(String response);

        void onError(String message);
    }

    // for UserFragment get queryset
    interface UserAppointmentsResponseListener {
        void onResponse(JSONObject response);

        void onError(String message);
    }

    // for UserFragment delete appointment
    interface AppointmentDeleteResponseListener {
        void onResponse(String response);

        void onError(String message);
    }

    // for SettingsActivity
    interface UserUpdateResponseListener {
        void onResponse(JSONObject response);

        void onError(String message);
    }

    public void logTheUser(String login, String password, LoginResponseListener responseListener) {
        System.out.println("messagePostRequest called() ----------------");

        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                // handle invalid response
                responseListener.onError(error.toString());
            }

        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(postRequest);
    }

    public void getBaseInfo(BaseInfoResponseListener baseInfoResponseListener) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, INFO_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                baseInfoResponseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                baseInfoResponseListener.onError(error.toString());
            }
        });
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(getRequest);
    }

    public void getFilteredAppointments(FilteredAppointmentsResponseListener responseListener, HashMap<String, String> queryParams) {
        Uri.Builder builder = Uri.parse(APPOINTMENTS_URL).buildUpon();
        if (queryParams.containsKey("personel_id"))
            builder.appendQueryParameter("personel_id", queryParams.get("personel_id"));
        if (queryParams.containsKey("data"))
            builder.appendQueryParameter("data", queryParams.get("data"));
        if (queryParams.containsKey("specjalnosc_id"))
            builder.appendQueryParameter("specjalnosc_id", queryParams.get("specjalnosc_id"));
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

    public void getAppointemntsSubpage(FilteredAppointmentsResponseListener responseListener, String subpageUrl) {
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

    public void bookAppointment(HashMap<String, Integer> queryParams, AppointmentBookResponseListener responseListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("termin", queryParams.get("termin"));
            postData.put("uzytkownik", queryParams.get("uzytkownik"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, BOOK_APPOINTMENT_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                // redirect response to whatever implements
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

    public void registerTheUser(HashMap<String, String> queryParams, RegisterResponseListener responseListener) {
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

    public void getUserAppointments(String userID, UserAppointmentsResponseListener responseListener) {
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

    public void deleteUserAppointment(String appointmentID, AppointmentDeleteResponseListener responseListener) {
        final int[] statusCode = new int[1];
        String URL = BOOKED_APPOINTMENTS_URL + appointmentID + '/';
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("status code from DataService: " + statusCode[0]);
                responseListener.onResponse(response);
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
            postData.put("id", putParams.get("id"));
            postData.put(AccountSettingsActivity.EMAIL_UPDATE_API, putParams.get(AccountSettingsActivity.EMAIL_UPDATE_API));
            postData.put(AccountSettingsActivity.OLD_PWD_UPDATE_API, putParams.get(AccountSettingsActivity.OLD_PWD_UPDATE_API));
            postData.put(AccountSettingsActivity.NEW_PWD_UPDATE_API, putParams.get(AccountSettingsActivity.NEW_PWD_UPDATE_API));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, USER_ACCOUNT_UPDATE_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                // redirect response to whatever implements
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
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(putRequest);
    }

    public void updatePersonalSettings(HashMap<String, String> putParams, JsonObjectResponseListener responseListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", putParams.get("id"));
            postData.put(PersonalSettingsActivity.FIRST_NAME_UPDATE_API, putParams.get(PersonalSettingsActivity.FIRST_NAME_UPDATE_API));
            postData.put(PersonalSettingsActivity.LAST_NAME_UPDATE_API, putParams.get(PersonalSettingsActivity.LAST_NAME_UPDATE_API));
            postData.put(PersonalSettingsActivity.PHONE_UPDATE_API, putParams.get(PersonalSettingsActivity.PHONE_UPDATE_API));
            postData.put(PersonalSettingsActivity.CITY_CODE_UPDATE_API, putParams.get(PersonalSettingsActivity.CITY_CODE_UPDATE_API));
            postData.put(PersonalSettingsActivity.CITY_UPDATE_API, putParams.get(PersonalSettingsActivity.CITY_UPDATE_API));
            postData.put(PersonalSettingsActivity.STREET_UPDATE_API, putParams.get(PersonalSettingsActivity.STREET_UPDATE_API));
            postData.put(PersonalSettingsActivity.HOUSE_NUMBER_UPDATE_API, putParams.get(PersonalSettingsActivity.HOUSE_NUMBER_UPDATE_API));
            System.out.println("postData --------------------------------------------------");
            System.out.println(postData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, USER_ACCOUNT_UPDATE_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                // redirect response to whatever implements
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
        HttpRequestSingleton.getInstance(context).getRequestQueue().add(putRequest);
    }

    // used in both Account/Personal-SettingsActivity to display current user's data
    public void userSettingsInfo(String ID, JsonObjectResponseListener responseListener) {
        String URL = USER_ACCOUNT_INFO_URL + ID + "/";
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
