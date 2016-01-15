package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.User;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class VerificationActivity extends AppCompatActivity {

    private static String DEFAULT_VALUE = "__DEFAULT__";
    private static String TAG = VerificationActivity.class.getSimpleName();

    String mUserID;
    String mAuthToken;
    JSONObject mUserJSON;
    User muser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Handling the incoming intent
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        // Check if data is null to prevent NullPointerException
        if(data != null) {
            String path = data.getPath();

            // Debug
            System.out.println(action);
            System.out.println(data);
            System.out.println(path);

            String url = Utils.SERVER_URL + Utils.VERIFY_PATH + path;
            mUserID = Utils.getFromSharedPrefs(VerificationActivity.this, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
            mAuthToken = Utils.getFromSharedPrefs(VerificationActivity.this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);

            Log.d(TAG, mUserID);
            Log.d(TAG, mAuthToken);

            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("x-auth-user", mUserID);
            reqHeaders.put("x-auth-token", mAuthToken);

            Req req = new Req(VerificationActivity.this, url);
            req.getWithHeader(reqHeaders, new Req.Res() {
                @Override
                public void onSuccess(JSONObject res) {
                    mUserJSON = res;
                    muser = Parse.user(mUserJSON);

                    if(!muser.isVerified()) {
                        setNegativeView();
                    }
                    Log.d(TAG, res.toString());
                }

                @Override
                public void onError(VolleyError error) {
                    setNegativeView();
                    Log.d(TAG, error.toString());
                }
            });

        }

        findViewById(R.id.yayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerificationActivity.this, ProfileActivity.class);
                if (mUserJSON != null) {
                    intent.putExtra("user", mUserJSON.toString());
                }
                startActivity(intent);
            }
        });
    }

    private void setNegativeView() {
        Button yayButton = (Button) findViewById(R.id.yayButton);
        TextView pathText = (TextView) findViewById(R.id.pathText);
        ImageView checkImageView = (ImageView) findViewById(R.id.checkImageView);

        yayButton.setText("Oh nein");
        pathText.setText("Verifikation fehlgeschlagen");
        checkImageView.setImageDrawable(getResources().getDrawable(R.drawable.frown_o));


    }
}
