package de.rfunk.hochschulify.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.User;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class ProfileActivity extends AppCompatActivity {


    static final String DEFAULT_VALUE = "__DEFAULT__";
    static final String TAG = ProfileActivity.class.getSimpleName();

    User mUser;
    JSONObject mUserJSON;
    JSONArray mTlds;
    String mUserID;
    String mAuthToken;

    Button mVerifyButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up request
        String tldUrl = Utils.SERVER_URL + Utils.TLD_PATH;
        final Req tldReq = new Req(ProfileActivity.this, tldUrl);

        // Retrieve list of accepted tlds from service
        // This is done here to make sure the list is present when the user
        // hits the "verify" button
        tldReq.get(new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                try {
                    // DEBUG
                    Log.d(TAG, res.toString());

                    mTlds = res.getJSONArray("tlds");
                } catch (JSONException e) {
                    // TODO: Error handling
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Error handling
            }
        });

        // Assign views
        mVerifyButton = (Button) findViewById(R.id.buttonVerify);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Profil");
        setSupportActionBar(toolbar);

        // Retrieve userID and authToken from shared preferences
        mUserID = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
        mAuthToken = Utils.getFromSharedPrefs(this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);

        // Check if userID and authToken are saved in shared preferences
        if (mUserID.equals(DEFAULT_VALUE) || mAuthToken.equals(DEFAULT_VALUE)) {
            // TODO: Error handling
            // Either userID or authToken not found in shared preferences
            // Redirect to login?
        }

        // Set up request
        String userUrl = Utils.SERVER_URL + Utils.USER_PATH + "/" + mUserID;
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("x-auth-user", mUserID);
        reqHeaders.put("x-auth-token", mAuthToken);

        // Retrieve user credentials from service
        Req userReq = new Req(this, userUrl);
        userReq.getWithHeader(reqHeaders, new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                mUserJSON = res;
                mUser = Parse.user(res);

                // Hide verify button if user is already verified
                if (!mUser.isVerified())
                    mVerifyButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Error handling
            }
        });

        // Set OnClickListener for verify Button
        mVerifyButton.setOnClickListener(mVerifyOnClick);


    }

    /**
     * Inflates edit icon to toolbar
     *
     * @param menu the menu
     * @return always returns true atm
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /**
     * OnClickListener for edit icon in toolbar.
     * Starts ProfileEditActivity and puts the user as an extra
     * on the intent.
     *
     * @param item the item
     * @return true on success or parent method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Intent intent = new Intent(this, ProfileEditActivity.class);
                intent.putExtra("user", mUserJSON.toString());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener mVerifyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Set up dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Verifiziere dich");
            builder.setMessage("Gib deine Mailadresse an der Hochschule ein, um dich verifizieren zu lassen");
            LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_profile_verify, null);
            builder.setView(dialogView);

            final EditText email = (EditText) dialogView.findViewById(R.id.email);

            //Set behaviour for positive button
            builder.setPositiveButton("Absenden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Check if eMail matches any of the whitelisted tlds
                    try {
                        if (checkTLD(email.getText().toString())) {
                            // mail is whitelisted, send verification request
                            String verifyUrl = Utils.SERVER_URL + Utils.VERIFY_PATH;

                            Map<String, String> reqHeader = new HashMap<String, String>();
                            reqHeader.put("x-auth-user", mUserID);
                            reqHeader.put("x-auth-token", mAuthToken);

                            JSONObject reqBody = new JSONObject();
                            // For now, all verification mails are sent to a dummy account
                            // For production, this code should be used
                            // reqBody.put("email", mUserJSON.get("email"));
                            reqBody.put("email", "eisclientdemo@gmail.com");

                            Req verifyReq = new Req(ProfileActivity.this, verifyUrl);
                            verifyReq.postWithHeader(reqBody, reqHeader, new Req.Res() {
                                @Override
                                public void onSuccess(JSONObject res) {
                                    System.out.println(res);
                                    // TODO: Give feedback on success
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    // TODO: error handling
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            // Set up negative button
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    /**
     * Checks if a given email matches any of the whitelisted tlds
     *
     * @param userMail mail to be checked
     * @return true if mail matches whitelisted tld, false otherwise
     * @throws JSONException
     */
    public boolean checkTLD (String userMail) throws JSONException {
        String regEx = ".*@.*";
        for(int j = 0; j < mTlds.length(); j++) {
            String st = regEx + Pattern.quote(mTlds.get(j).toString());
            System.out.println(st);
            Pattern pattern = Pattern.compile(st);
            Matcher matcher = pattern.matcher(userMail);
            if(matcher.matches()) {
                return true;
            }
        }
        return false;
    }
}
