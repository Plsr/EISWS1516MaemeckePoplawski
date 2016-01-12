package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Entry;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;


public class WriteThreadActivity extends AppCompatActivity {

    EditText mThreadTitle;
    EditText mThreadText;
    Spinner mSpinner;

    String mTitle;
    String mText;
    String mType;
    String mCourse;

    Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_thread);

        // Receive extras from intent
        mExtras = getIntent().getExtras();
        mCourse = mExtras.getString("COURSE_ID");

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        // Set up Spinner
        mSpinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.post_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        // Get EditText Views
        mThreadTitle = (EditText) findViewById(R.id.thread_title);
        mThreadText = (EditText) findViewById(R.id.thread_input);

        // Set onClickListener for Send Button
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = mThreadTitle.getText().toString();
                mText = mThreadText.getText().toString();
                mType = mSpinner.getSelectedItem().toString();

                if(Utils.isEmptyString(mTitle)) {
                    System.out.println("Title is empty");
                    //TODO: Display Error Message on title input
                } else if(Utils.isEmptyString(mText)) {
                    System.out.println("Text is empty");
                    //TODO: Display Error Message on text input
                } else {
                    Entry mEntry = new Entry();
                    mEntry.setTitle(mTitle);
                    mEntry.setText(mText);
                    mEntry.setType(mType);
                    mEntry.setCourse(mCourse);

                    System.out.println(mTitle);
                    System.out.println(mText);
                    System.out.println(mType);
                    System.out.println(mCourse);

                    sendThread(mEntry);
                }
            }
        });
    }

    private void sendThread(Entry entry) {
        String DEFAULT = "__DEFAULT__";
        String url = Utils.SERVER_URL + Utils.ENTRY_PATH;
        Map<String, String> reqHeaders = new HashMap<String, String>();

        // Set up Header
        String userAuth = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, DEFAULT);
        String authToken = Utils.getFromSharedPrefs(this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT);
        System.out.println(userAuth);
        System.out.println(authToken);

        reqHeaders.put("x-auth-user", userAuth);
        reqHeaders.put("x-auth-token", authToken);

        System.out.println(reqHeaders.get("x-auth-user"));
        System.out.println(reqHeaders.get("x-auth-token"));

        //Set up Body
        JSONObject reqBody = new JSONObject();
        try {
            reqBody.put("title", entry.getTitle());
            reqBody.put("text", entry.getText());
            reqBody.put("type", entry.getType());
            reqBody.put("course", entry.getCourse());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Req req = new Req(this, url);
        req.postWithHeader(reqBody, reqHeaders, new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                System.out.println("WOOOOH");
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }


}
