package de.rfunk.hochschulify.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.services.RegistrationIntentService;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;


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

                    String url = Utils.SERVER_URL + Utils.USER_PATH + "/" + loginCredentials;

                    Req userAuth = new Req(SplashscreenActivity.this, url);
                    Map<String, String> requestHeaders = new HashMap<String, String>();
                    requestHeaders.put("x-auth-user", loginCredentials);
                    requestHeaders.put("x-auth-token", authToken);
                    userAuth.getWithHeader(requestHeaders, new Req.Res() {
                        @Override
                        public void onSuccess(JSONObject res) {
                            System.out.println("YAY");
                            System.out.println(res);
                            if(res.has("status")) {
                                Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(SplashscreenActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }


                        }

                        @Override
                        public void onError(VolleyError error) {
                            System.out.println("OH NOES");
                            Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

                }
                else {
                    Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, 3000);





    }
}
