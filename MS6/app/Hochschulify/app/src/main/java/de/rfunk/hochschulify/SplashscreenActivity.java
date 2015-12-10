package de.rfunk.hochschulify;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashscreenActivity extends AppCompatActivity {

    private static final String DEFAULT_VALUE = "__DEFAULT__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Forces the Splash screen to show 3 Seconds.
        // Only for testing reasons, delete later
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] keys = {Utils.LOGIN_AUTHTOKEN_KEY, Utils.LOGIN_USERNAME_KEY};

                // Check if there are values in the shared preferences for all requested keys.
                if(Utils.credentialsInSharedMem(SplashscreenActivity.this, keys)){
                    // DEBUG
                    System.out.println("Shared Preferences found");

                    // Get the values of the requested keys.
                    String loginCredentials = Utils.getFromSharedPrefs(SplashscreenActivity.this, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
                    String authToken = Utils.getFromSharedPrefs(SplashscreenActivity.this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);

                    //DEBUG
                    System.out.println(loginCredentials);
                    System.out.println(authToken);

                    // TODO: Check if credentials still valid
                    // TODO: Determine which activity to show based on credentials
                }

                Intent intent = new Intent(SplashscreenActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 3000);





    }
}
