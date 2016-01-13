package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

    JSONObject mUSer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            }

            @Override
            public void onError(VolleyError error) {

            }
        });


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
                intent.putExtra("user", mUSer.toString());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
