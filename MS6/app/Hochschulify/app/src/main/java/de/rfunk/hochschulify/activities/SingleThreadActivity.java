package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.Utils;

public class SingleThreadActivity extends AppCompatActivity {

    // Static ThreadID for development build
    // This ID will change on DB reset, remember to change it accordingly
    public static final String THREAD_ID = "568ea39fd34ca81b07219f55";

    // Setting up service data
    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String ENTRY_PATH = Utils.ENTRY_PATH;

    // Declare views to be filled with data
    TextView threadTitleView;
    TextView threadBodyView;
    TextView threadAuthorView;

    String threadAuthor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thread);

        // Assign concrete views to variables
        threadTitleView = (TextView) findViewById(R.id.thread_title);
        threadBodyView = (TextView) findViewById(R.id.thread_body);
        threadAuthorView = (TextView) findViewById(R.id.author);

        try {
            setEntry(THREAD_ID, threadTitleView, threadBodyView, threadAuthorView);
        } catch (JSONException e) {
            e.printStackTrace();
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
                startActivity(intent);
            }
        });
    }

    /**
     * Gets the content of a given thread from the service by ThreadID.
     * Sets values in TextViews after the request has finished.
     *
     * TODO: Error handling
     * TODO: Comments count
     *
     * @param entryID ID of the Thread to be requested at service
     * @param title   TextView that shall be filled with the title of the thread
     * @param body    TextView that shall be filled with the body of the thread
     * @throws JSONException
     */
    public void setEntry(String entryID, final TextView title, final TextView body, final TextView author) throws JSONException {
        // Set up request
        // Thread ID is sent in the URL, no body required
        final JSONObject reqBody = new JSONObject();
        String url = SERVER_URL + ENTRY_PATH + "/" + entryID;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Pares response from service
                    String rTitle = response.get("title").toString();
                    String rText = response.get("text").toString();
                    String rType = response.get("type").toString();
                    String rCourse = response.get("course").toString(); // Is this needed here?
                    String rParententry = response.get("parententry").toString();
                    JSONObject rUser = (JSONObject) response.get("user");
                    String rUserName = rUser.get("name").toString();
                    // TODO: Deal with subentries

                    // DEBUG
                    System.out.println(rTitle);
                    System.out.println(rText);
                    System.out.println(rType);
                    System.out.println(rUser);
                    System.out.println(rCourse);
                    System.out.println(rParententry);

                    // Check if all Strings have content
                    // If one of them is corrupted, nothing at all will be displayed
                    if(!(Utils.isEmptyString(rTitle) && Utils.isEmptyString(rText) && Utils.isEmptyString(rUserName))) {
                        title.setText(rTitle);
                        body.setText(rText);
                        author.setText(rUserName);
                        threadAuthor = rUserName;
                    } else {
                        // TODO: Offer a method to retry the action.
                    }
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
}