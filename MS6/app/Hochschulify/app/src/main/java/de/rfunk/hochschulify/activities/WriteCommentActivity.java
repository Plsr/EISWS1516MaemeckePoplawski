package de.rfunk.hochschulify.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Entry;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class WriteCommentActivity extends AppCompatActivity {

    private static final String TYPE = "ANDERS";
    private static final String TAG = WriteCommentActivity.class.getSimpleName();

    TextView answerTo;
    EditText mThreadText;

    String parentAuthor;
    String mText;
    String mCourse;
    String mParentID;
    Bundle intentExtras;
    JSONObject mParentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        intentExtras = getIntent().getExtras();
        String jsonString = intentExtras.getString("parent");

        Log.d(TAG, jsonString);

        try {
            mParentEntry = new JSONObject(jsonString);
            Log.d(TAG, mParentEntry.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parentAuthor = mParentEntry.getJSONObject("user").getString("name");
            mCourse = mParentEntry.getString("course");
            mParentID = mParentEntry.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Antwort schreiben");
        setSupportActionBar(toolbar);

        answerTo = (TextView) findViewById(R.id.answer_to);
        answerTo.setText("Antwort an " + parentAuthor + ":");
        mThreadText = (EditText) findViewById(R.id.thread_input);

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mText = mThreadText.getText().toString();

               if(Utils.isEmptyString(mText)) {
                    System.out.println("Text is empty");
                    //TODO: Display Error Message on text input
                } else {
                    // Set up entry
                    Entry mEntry = new Entry();
                    mEntry.setText(mText);
                    mEntry.setType(TYPE);
                    mEntry.setCourse(mCourse);
                    mEntry.setParentEntry(mParentID);

                    sendThread(mEntry);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Intent intent = new Intent(this, ProfileEditActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendThread(Entry entry) {
        String DEFAULT = "__DEFAULT__";
        String url = Utils.SERVER_URL + Utils.ENTRY_PATH;
        Map<String, String> reqHeaders = new HashMap<String, String>();

        // Set up Header
        String userAuth = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, DEFAULT);
        String authToken = Utils.getFromSharedPrefs(this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT);
        reqHeaders.put("x-auth-user", userAuth);
        reqHeaders.put("x-auth-token", authToken);

        // Set up Body
        JSONObject reqBody = new JSONObject();
        try {
            reqBody.put("text", entry.getText());
            reqBody.put("type", entry.getType());
            reqBody.put("course", entry.getCourse());
            reqBody.put("parententry", entry.getParentEntry());
            

            Log.d(TAG, reqBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set up request
        Req req = new Req(this, url);
        req.postWithHeader(reqBody, reqHeaders, new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                // Display Toast
                Context context = getApplicationContext();
                CharSequence toastText = "Erfolgreich gepostet";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastText, duration);
                toast.show();

                // Open SingleThreadActivity with created entry
                Intent intent = new Intent(WriteCommentActivity.this, SingleThreadActivity.class);
                intent.putExtra("entry", res.toString());
                startActivity(intent);
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Error handling
            }
        });
    }
}
