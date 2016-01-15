package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CommentsAdapter;
import de.rfunk.hochschulify.adapters.CourseBookmarkAdapter;
import de.rfunk.hochschulify.adapters.CourseOverviewAdapter;
import de.rfunk.hochschulify.pojo.Entry;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class SingleThreadActivity extends AppCompatActivity implements CommentsAdapter.CommentsAdapterInterface {

    // TODO: Documentation

    // Static ThreadID for development build
    // This ID will change on DB reset, remember to change it accordingly
    public static final String THREAD_ID = "568ea39fd34ca81b07219f55";

    private static final String TAG = SingleThreadActivity.class.getSimpleName();

    // Setting up service data
    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String ENTRY_PATH = Utils.ENTRY_PATH;

    // Declare views to be filled with data
    TextView mThreadTitleView;
    TextView mThreadBodyView;
    TextView mThreadAuthorView;
    TextView mCommentsCount;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    String threadAuthor;
    List<Entry> mSubEntriesList = new ArrayList<Entry>();

    Bundle mIntentExtras;
    JSONObject mEntryJSON;
    Entry mEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thread);

        Log.d(TAG, "Called");

        // Receive intent extras
        mIntentExtras = getIntent().getExtras();
        String jsonString = mIntentExtras.getString("entry");

        // Assign concrete views to variables
        mThreadTitleView = (TextView) findViewById(R.id.thread_title_input);
        mThreadBodyView = (TextView) findViewById(R.id.thread_body);
        mThreadAuthorView = (TextView) findViewById(R.id.author);
        mCommentsCount = (TextView) findViewById(R.id.comments_headline);

        // Receive first level entry from extras and
        // set it
        if(!(Utils.isEmptyString(jsonString))) {

            try {
                mEntryJSON = new JSONObject(jsonString);
                JSONObject user = mEntryJSON.getJSONObject("user");
            } catch (JSONException e) {
                e.printStackTrace();
            }

                mEntry = Parse.entry(mEntryJSON);

                // Remove title text view from layout if there is no title
                if(mEntry.getTitle().equals("")) {
                    mThreadTitleView.setVisibility(View.GONE);
                } else {
                    mThreadTitleView.setText(mEntry.getTitle());
                }

                // Set other content
                mThreadBodyView.setText(mEntry.getText());
                mThreadAuthorView.setText(mEntry.getAuthor().getName());

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up toolbar
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // Set onClickListener for Back Button
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // OnClickListener for "Antwort schreiben" Button
        // Sends the ID of the current thread to new Activity
        findViewById(R.id.button_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleThreadActivity.this, WriteCommentActivity.class);
                intent.putExtra("parentEntry", THREAD_ID);
                intent.putExtra("parentAuthor", threadAuthor);
                intent.putExtra("parent", mEntryJSON.toString());
                startActivity(intent);
            }
        });

        // TODO: Display loading indicator
        // TODO: Display comments

        getSubentries(mEntry.getId());
    }

    private void getSubentries(String entryID) {
        Log.d(TAG, "getSubentries called");
        String url = Utils.SERVER_URL + Utils.ENTRY_PATH + "/" + entryID;
        Req req = new Req(this, url);
        req.get(new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                Log.d(TAG + " response", res.toString());
                try {
                    JSONArray subentries = res.getJSONArray("subentries");
                    Log.d(TAG + " subentries", subentries.toString());
                    for (int i = 0; i < subentries.length(); i++) {
                        JSONObject jsonEntry = subentries.getJSONObject(i);
                        Log.d(TAG + " single subentry", subentries.getJSONObject(i).toString());
                        Entry tmpEntry = Parse.entry(jsonEntry);
                        Log.d(TAG + " entry object", tmpEntry.toString());
                        Log.d(TAG + " entry object text", tmpEntry.getText());
                        mSubEntriesList.add(tmpEntry);
                    }

                    mCommentsCount.setText("Kommentare (" + mSubEntriesList.size() + ")");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpRecyclerView();


            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }


    public void setUpRecyclerView() {
        // TODO: Display info if there are no comments
        Log.d(TAG, "setupRecView");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commentsList);
        Log.d(TAG, recyclerView.toString());
        recyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(this);
        Log.d(TAG, mLayoutManager.toString());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentsAdapter(this, mSubEntriesList, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {

    }
}