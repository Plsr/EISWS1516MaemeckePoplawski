package de.rfunk.hochschulify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleThreadActivity extends AppCompatActivity {

    public static final String THREAD_ID = "5683b16de86eec0932e37fac";

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String ENTRY_PATH = Utils.ENTRY_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thread);

        try {
            getMainEntry(THREAD_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ein Thread");
        setSupportActionBar(toolbar);

        findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleThreadActivity.this, WriteCommentActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getMainEntry(String entryID) throws JSONException {
        // Set up request
        // Thread ID is sent in the URL, no body required
        JSONObject reqBody = new JSONObject();
        String url = SERVER_URL + ENTRY_PATH + "/" + entryID;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Pares response from service
                    String title = response.get("title").toString();
                    String text =  response.get("text").toString();
                    String type = response.get("type").toString();
                    String user = response.get("user").toString();
                    String course = response.get("course").toString(); // Is this needed here?
                    String parententry = response.get("parententry").toString();
                    // TODO: Deal with subentries

                    // DEBUG
                    System.out.println(title);
                    System.out.println(text);
                    System.out.println(type);
                    System.out.println(user);
                    System.out.println(course);
                    System.out.println(parententry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add Request to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }
}
