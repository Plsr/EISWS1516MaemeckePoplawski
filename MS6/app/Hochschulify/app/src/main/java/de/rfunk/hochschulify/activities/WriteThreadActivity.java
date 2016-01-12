package de.rfunk.hochschulify.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Entry;
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

                    System.out.println(mTitle);
                    System.out.println(mText);
                    System.out.println(mType);
                    System.out.println(mCourse);

                }
            }
        });
    }

    private void sendThread() {

    }


}
