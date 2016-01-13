package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.utils.Utils;

public class SingleThreadActivity extends AppCompatActivity {

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

    String threadAuthor;

    Bundle mIntentExtras;
    JSONObject mEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thread);

        // Receive intent extras
        mIntentExtras = getIntent().getExtras();
        String jsonString = mIntentExtras.getString("entry");

        // Assign concrete views to variables
        mThreadTitleView = (TextView) findViewById(R.id.thread_title_input);
        mThreadBodyView = (TextView) findViewById(R.id.thread_body);
        mThreadAuthorView = (TextView) findViewById(R.id.author);

        // Receive first level entry from extras and
        // set it
        if(!(Utils.isEmptyString(jsonString))) {
            try {
                mEntry = new JSONObject(jsonString);
                JSONObject user = mEntry.getJSONObject("user");
                mThreadTitleView.setText(mEntry.getString("title"));
                mThreadBodyView.setText(mEntry.getString("text"));
                mThreadAuthorView.setText(user.getString("name"));

                Log.d(TAG, mEntry.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                intent.putExtra("parent", mEntry.toString());
                startActivity(intent);
            }
        });

        // TODO: Display loading indicator
        // TODO: Display comments
    }
}