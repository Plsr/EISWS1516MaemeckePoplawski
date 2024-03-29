package de.rfunk.hochschulify.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.pojo.Entry;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * Helper class for making requests
 */
public class Req {

    // TODO: change all [method]WithHeaders to [method]WithOut since we don't need to send
    // other headers than our x-auth-... headers.

    private static final String DEFAULT_VALUE = "__DEFAULT__";
    private static final String TAG = Req.class.getSimpleName();

    private String mURL;
    private Context mContext;

    public Req(Context context, String URL) {
        mURL = URL;
        mContext = context;
    }

    public interface Res {
        void onSuccess(JSONObject res);
        void onError(VolleyError error);
    }

    /**
     * Retrieves userID and authToken from shared preferences and writes them in
     * a Map to be used in requests
     *
     * @return authHeaders if found, null otherwise
     */
    private Map<String, String> getAuthHeaders() {

        final Map<String, String> authHeaders = new HashMap<String, String>();
        // Set up authHeaders
        String userAuth = Utils.getFromSharedPrefs(mContext, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
        String authToken = Utils.getFromSharedPrefs(mContext, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);
        authHeaders.put("x-auth-user", userAuth);
        authHeaders.put("x-auth-token", authToken);

        if (userAuth.equals(DEFAULT_VALUE) || authToken.equals(DEFAULT_VALUE)) {
            return null;
        } else {
            return authHeaders;
        }
    }

    // Make a get Request and call the passed response object
    public void get(final Req.Res res) {
        final JSONObject reqBody = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, mURL, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    public void getWithHeader(final Map<String, String> headers, final Req.Res res) {
        final JSONObject reqBody = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, mURL, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    public void getWithAuth(final Req.Res res) {
        final Map<String, String> reqHeaders = getAuthHeaders();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, mURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return reqHeaders;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    public void post(JSONObject reqBody, final Req.Res res) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, mURL, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    public void postWithHeader(JSONObject reqBody, final Map<String, String> headers, final Req.Res res) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, mURL, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                System.out.println("Header in POST: " + headers.get("x-auth-user"));
                System.out.println("Header in POST: " + headers.get("x-auth-token"));
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    public void putWithAuth(JSONObject reqBody, final Req.Res res) {

        final Map<String, String> reqHeaders = new HashMap<String, String>();
        // Set up Header
        String userAuth = Utils.getFromSharedPrefs(mContext, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
        String authToken = Utils.getFromSharedPrefs(mContext, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);
        reqHeaders.put("x-auth-user", userAuth);
        reqHeaders.put("x-auth-token", authToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, mURL, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                res.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return reqHeaders;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }
}
