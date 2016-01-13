package de.rfunk.hochschulify.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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


public class WriteThreadActivity extends AppCompatActivity {

    private static final String DEFAULT_VALUE = "__DEFAULT__";
    private static final String TAG = WriteThreadActivity.class.getSimpleName();

    EditText mThreadTitle;
    EditText mThreadText;
    Spinner mSpinner;
    ImageView thumbsUp;
    ImageView thumbsDown;

    String mTitle;
    String mText;
    String mType;
    String mCourse;
    String[] mSpinnerContent;
    JSONObject mUser;



    Boolean recommended = true;

    Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_thread);

        // Receive extras from intent
        mExtras = getIntent().getExtras();
        mCourse = mExtras.getString("COURSE_ID");

        thumbsUp = (ImageView) findViewById(R.id.thumbs_up);
        thumbsDown = (ImageView) findViewById(R.id.thumbs_down);

        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getRecommended()) {
                    setRecommended(true);
                    thumbsUp.setImageResource(R.drawable.thumbs_up_active);
                    thumbsDown.setImageResource(R.drawable.thumbs_down);
                }
            }
        });

        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRecommended()) {
                    setRecommended(false);
                    thumbsDown.setImageResource(R.drawable.thumbs_down_active);
                    thumbsUp.setImageResource(R.drawable.thumbs_up);
                }
            }
        });

        try {
            mUser = Utils.getCurrentUser(this, DEFAULT_VALUE);
            Log.d(TAG, mUser.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        try {
            mSpinnerContent = setUpSpinnerContentsForType(mUser.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSpinnerContent == null) {
            mSpinnerContent = new String[]{"ANDERS"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinnerContent);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, mSpinner.getSelectedItem().toString());
                String curSelection = mSpinner.getSelectedItem().toString();
                LinearLayout recommendLayout = (LinearLayout) findViewById(R.id.recommendLayout);
                if (curSelection.equals("ERFAHRUNG")) {
                    recommendLayout.setVisibility(View.VISIBLE);
                } else {
                    recommendLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

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
                    // Set up entry
                    Entry mEntry = new Entry();
                    mEntry.setTitle(mTitle);
                    mEntry.setText(mText);
                    mEntry.setType(mType);
                    mEntry.setCourse(mCourse);
                    mEntry.setRecommendation(recommended);

                    sendThread(mEntry);
                }
            }
        });
    }


    /**
     * Function sends a given thread to the service.
     * Note that the function does not check if the entry is valid before sending it.
     *
     * @param entry Entry to be send to the service
     */
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
            reqBody.put("title", entry.getTitle());
            reqBody.put("text", entry.getText());
            reqBody.put("type", entry.getType());
            reqBody.put("course", entry.getCourse());
            reqBody.put("recommendation", entry.isRecommendation());
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
                Intent intent = new Intent(WriteThreadActivity.this, SingleThreadActivity.class);
                intent.putExtra("entry", res.toString());
                startActivity(intent);
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Error handling
            }
        });
    }

    public String[] setUpSpinnerContentsForType(String type) {
        if (type.equals("STUDENT")) {
            return new String[]{"ERFAHRUNG", "ANDERS"};
        }
        if (type.equals("ALUMNI")) {
            return new String[]{"ALUMNIBERICHT", "ANDERS"};
        }
        if (type.equals("INTERESSENT")) {
            return new String[]{"ANDERS"};
        }
        return new String[0];
    }


    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

}
