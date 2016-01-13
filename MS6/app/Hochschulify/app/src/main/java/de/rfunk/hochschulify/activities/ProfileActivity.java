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

    Toolbar toolbar;
    static final String DEFAULT_VALUE = "__DEFAULT__";

    User mUser;
    JSONObject mUserJSON;
    JSONArray mTlds;
    Button mVerifyButton;
    String mUserID;
    String mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String tldUrl = Utils.SERVER_URL + Utils.TLD_PATH;
        final Req tldReq = new Req(ProfileActivity.this, tldUrl);

        tldReq.get(new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                try {
                    Log.d("FICKIFICK", res.toString());
                    mTlds = res.getJSONArray("tlds");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        mVerifyButton = (Button) findViewById(R.id.buttonVerify);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        setSupportActionBar(toolbar);

        mUserID = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
        mAuthToken = Utils.getFromSharedPrefs(this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);
        String userUrl = Utils.SERVER_URL + Utils.USER_PATH + "/" + mUserID;
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("x-auth-user", mUserID);
        reqHeaders.put("x-auth-token", mAuthToken);

        Req userReq = new Req(this, userUrl);
        userReq.getWithHeader(reqHeaders, new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                mUserJSON = res;
                mUser = Parse.user(res);

                if (mUser.isVerified())
                    mVerifyButton.setVisibility(View.GONE);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });



        mVerifyButton.setOnClickListener(mVerifyOnClick);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

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



            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Verifiziere dich");
            builder.setMessage("Gib deine Mailadresse an der Hochschule ein, um dich verifizieren zu lassen");
            LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_profile_verify, null);
            builder.setView(dialogView);

            final EditText email = (EditText) dialogView.findViewById(R.id.email);

            builder.setPositiveButton("Absenden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Hier Email checken und Request abschicken
                    try {
                        if (checkTLD(email.getText().toString())) {
                            // Send request to verify
                            String verifyUrl = Utils.SERVER_URL + Utils.VERIFY_PATH;

                            Map<String, String> reqHeader = new HashMap<String, String>();
                            reqHeader.put("x-auth-user", mUserID);
                            reqHeader.put("x-auth-token", mAuthToken);

                            JSONObject reqBody = new JSONObject();
                            reqBody.put("email", "eisclientdemo@gmail.com");

                            Req verifyReq = new Req(ProfileActivity.this, verifyUrl);
                            verifyReq.postWithHeader(reqBody, reqHeader, new Req.Res() {
                                @Override
                                public void onSuccess(JSONObject res) {
                                    System.out.println(res);
                                }

                                @Override
                                public void onError(VolleyError error) {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

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
