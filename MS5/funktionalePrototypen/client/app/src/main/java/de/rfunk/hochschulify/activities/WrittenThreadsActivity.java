package de.rfunk.hochschulify.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.rfunk.hochschulify.R;

public class WrittenThreadsActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiritten_threads);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meine Threads");
        setSupportActionBar(toolbar);
    }
}
