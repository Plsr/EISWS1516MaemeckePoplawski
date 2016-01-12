package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CourseOverviewAdapter;
import de.rfunk.hochschulify.utils.Utils;
import de.rfunk.hochschulify.pojo.Entry;
import java.util.ArrayList;
import java.util.List;

public class CourseOverviewActivity extends AppCompatActivity implements CourseOverviewAdapter.EntryAdapterInterface {



    // ID of dummy course for development
    public static final String COURSE_ID = "5694f9550beaa63b4ef4e3d5";

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String COURSE_PATH = Utils.COURSE_PATH;

    Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Entry> mEntries = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setThreads();


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

                try {
                    JSONObject rUniversity = response.getJSONObject("university");

                    String rUniName = rUniversity.getString("name");
                    String rCourseName = response.getString("name");
                    JSONArray entries = response.getJSONArray("entries");

                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject iEntry = entries.getJSONObject(i);
                        JSONObject iUser = iEntry.getJSONObject("user");
                        String author = iUser.getString("name");
                        String title = iEntry.getString("title");
                        String text = iEntry.getString("text");
                        String id = iEntry.getString("_id");
                        String link = iEntry.getJSONObject("link").getString("self");
                        int subCount = 3;

                        System.out.println(author);
                        System.out.println(title);
                        System.out.println(text);

                        Entry xEntry = new Entry(title, text, author, subCount, link, id);
                        mEntries.add(xEntry);
                    }

                    // Set up Toolbar
                    toolbar.setTitle(rCourseName);
                    toolbar.setSubtitle(rUniName);
                    setSupportActionBar(toolbar);

                    setUpRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



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

    @Override
    public void onItemClick(int position) {
        //TODO: Do stuff
        Intent intent = new Intent(CourseOverviewActivity.this, SingleThreadActivity.class);
        intent.putExtra("ID", mEntries.get(position).getId());
        startActivity(intent);

    }

    public void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true); //Needed?
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CourseOverviewAdapter(this, mEntries, this);
        recyclerView.setAdapter(mAdapter);
    }
}
