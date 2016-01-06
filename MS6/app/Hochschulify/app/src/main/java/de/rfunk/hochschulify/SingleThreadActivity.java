package de.rfunk.hochschulify;

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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleThreadActivity extends AppCompatActivity {

    public static final String THREAD_ID = "5683b16de86eec0932e37fac";

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String ENTRY_PATH = Utils.ENTRY_PATH;
    public static final String USER_PATH = Utils.USER_PATH;

    // Declare views to be filled with data
    TextView threadTitle;
    TextView threadBody;
    TextView threadAuthor;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thread);

        // Assign concrete views to variables
        threadTitle = (TextView) findViewById(R.id.thread_title);
        threadBody = (TextView) findViewById(R.id.thread_body);
        threadAuthor = (TextView) findViewById(R.id.author);

        try {
            setEntry(THREAD_ID, threadTitle, threadBody, threadAuthor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: Implement Back Arrow
        // TODO: Set title
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Gets the content of a given thread from the service by ThreadID.
     * Sets values in TextViews after the request has finished.
     *
     * TODO: Error handling
     * TODO: Set Author Field
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
                    String rUser = response.get("user").toString();
                    String rCourse = response.get("course").toString(); // Is this needed here?
                    String rParententry = response.get("parententry").toString();
                    // TODO: Deal with subentries

                    // DEBUG
                    System.out.println(rTitle);
                    System.out.println(rText);
                    System.out.println(rType);
                    System.out.println(rUser);
                    System.out.println(rCourse);
                    System.out.println(rParententry);

                    // TODO: Check if empty
                    title.setText(rTitle);
                    body.setText(rText);
                    setEntryAuthor(rUser, author);
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

    /**
     * Gets the content of a given thread from the service by ThreadID.
     * Sets values in TextViews after the request has finished.
     *
     * TODO: Error handling
     * TODO: Display Username or email?
     *
     * @param userId            ID of the User to be requested at service
     * @param authorTextView    TextView that shall be filled with the username of the thread
     * @throws JSONException
     */
    public void setEntryAuthor(String userId, final TextView authorTextView) throws JSONException{
        final JSONObject reqBody = new JSONObject();
        String url = SERVER_URL + USER_PATH + "/" + userId;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, reqBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rId = response.get("_id").toString();
                    String rType = response.get("type").toString();
                    String rName = response.get("name").toString();
                    Boolean rVerified = response.getBoolean("verified"); //TODO: does this work?

                    // DEBUG
                    System.out.println(rId);
                    System.out.println(rType);
                    System.out.println(rName);
                    System.out.println(rVerified);


                    authorTextView.setText(rName);
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