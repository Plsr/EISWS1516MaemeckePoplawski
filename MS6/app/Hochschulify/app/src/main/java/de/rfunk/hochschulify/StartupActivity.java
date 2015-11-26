package de.rfunk.hochschulify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    public void startRegister(View view) {
        //TODO: In emulator this causes a skip of 100-300 Frames, check behaviour on real device
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
