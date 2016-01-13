package de.rfunk.hochschulify.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    static final String DEFAULT_VALUE = "__DEFAULT__";

    JSONObject mUser;
    Button mVerifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mVerifyButton = (Button) findViewById(R.id.buttonVerify);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        setSupportActionBar(toolbar);

        String user = Utils.getFromSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, DEFAULT_VALUE);
        String authToken = Utils.getFromSharedPrefs(this, Utils.LOGIN_AUTHTOKEN_KEY, DEFAULT_VALUE);
        String url = Utils.SERVER_URL + Utils.USER_PATH + "/" + user;
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("x-auth-user", user);
        reqHeaders.put("x-auth-token", authToken);

        Req req = new Req(this, url);
        req.getWithHeader(reqHeaders, new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                System.out.println(res);
                mUser = res;
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
                intent.putExtra("user", mUser.toString());
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
}
