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

public class CourseOverviewActivity extends AppCompatActivity {

    // ID of dummy course for development
    public static final String COURSE_ID = "568ea023d98c388206afee74";

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String COURSE_PATH = Utils.COURSE_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);

        setThreads();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Medieninformatik");
        toolbar.setSubtitle("TH Köln");
        setSupportActionBar(toolbar);

        findViewById(R.id.card1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseOverviewActivity.this, SingleThreadActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(CourseOverviewActivity.this, WriteThreadActivity.class);
                startActivity(writeIntent);
            }
        });
    }

    public void setThreads() {
        // Set up request
        // Thread ID is sent in the URL, no body required
        final JSONObject reqBody = new JSONObject();
        String url = SERVER_URL + COURSE_PATH + "/" + COURSE_ID;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Pares response from service
                System.out.println(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Error handling
            }
        });

        // Add Request to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

}
