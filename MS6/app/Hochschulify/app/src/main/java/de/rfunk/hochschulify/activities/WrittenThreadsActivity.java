package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CourseOverviewAdapter;
import de.rfunk.hochschulify.pojo.Entry;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class WrittenThreadsActivity extends AppCompatActivity implements CourseOverviewAdapter.EntryAdapterInterface {

    Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Entry> mEntries = new ArrayList<Entry>();
    private List<JSONObject> mJSONEntries = new ArrayList<JSONObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiritten_threads);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meine Threads");
        setSupportActionBar(toolbar);

        setThreads();

    }

    public void setThreads() {
        // Set up request
        // Thread ID is sent in the URL, no body required
        String userId = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, "");
        String url = Utils.SERVER_URL + Utils.USER_PATH + "/" + userId + "?entries";

        Req req = new Req(this, url);
        req.get(new Req.Res() {

            @Override
            public void onSuccess(JSONObject res) {
                try {
                    JSONArray entries = res.getJSONArray("entries");
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject jsonEntry = entries.getJSONObject(i);
                        Entry entry = Parse.entry(jsonEntry);
                        mEntries.add(entry);
                        jsonEntry.put("course", entry.getCourse());
                        mJSONEntries.add(jsonEntry);

                        // TODO: Duplicated Arrays
                    }

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

    public void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CourseOverviewAdapter(this, mEntries, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(WrittenThreadsActivity.this, SingleThreadActivity.class);
        intent.putExtra("ID", mEntries.get(position).getId());
        intent.putExtra("entry", mJSONEntries.get(position).toString());
        startActivity(intent);
    }
}
