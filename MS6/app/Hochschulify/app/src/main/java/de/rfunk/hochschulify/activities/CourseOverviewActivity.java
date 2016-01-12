package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.pojo.University;
import de.rfunk.hochschulify.pojo.User;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;
import de.rfunk.hochschulify.pojo.Entry;
import java.util.ArrayList;
import java.util.List;

public class CourseOverviewActivity extends AppCompatActivity implements CourseOverviewAdapter.EntryAdapterInterface {



    // ID of dummy course for development
    public static final String COURSE_ID = "5694c6c88eb4cf9967bae56c";

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
        String url = SERVER_URL + COURSE_PATH + "/" + COURSE_ID;

        Req req = new Req(this, url);
        req.get(new Req.Res() {

            @Override
            public void onSuccess(JSONObject res) {
                try {
                    Course course = Parse.course(res);
                    JSONArray entries = res.getJSONArray("entries");
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject jsonEntry = entries.getJSONObject(i);
                        Entry entry = Parse.entry(jsonEntry);
                        mEntries.add(entry);
                    }

                    toolbar.setTitle(course.getName());
                    toolbar.setSubtitle(course.getUniversity().getName());
                    setSupportActionBar(toolbar);
                    setUpRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Maybe Errorhandling?
            }
        });
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
