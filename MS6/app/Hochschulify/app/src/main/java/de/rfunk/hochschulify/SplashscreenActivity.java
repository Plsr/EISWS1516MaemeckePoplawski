package de.rfunk.hochschulify;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashscreenActivity extends AppCompatActivity {

    // TODO: Disable possibility to use back button to return to this screen.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Forces the Splash screen to show 3 Seconds.
        // Only for testing reasons, delete later
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Check if user has credentials saved in shared preferences
                // TODO: Check if credentials still valid
                // TODO: Determine which activity to show based on credentials
                Intent intent = new Intent(SplashscreenActivity.this, StartupActivity.class);
                startActivity(intent);
            }
        }, 3000);





    }
}
