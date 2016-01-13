package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.User;
import de.rfunk.hochschulify.utils.Parse;

public class ProfileEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil bearbeiten");
        setSupportActionBar(toolbar);


        Spinner spinner = (Spinner) findViewById(R.id.profile_type_spinner);
        int arrayType = R.array.profile_types_normal;


        Bundle args = getIntent().getExtras();
        User user = new User();
        try {
            user = Parse.user(new JSONObject(args.getString("user")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (user.isVerified()) {
            arrayType = R.array.profile_types_verified;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayType, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Intent intent = new Intent(this, ProfileEditActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
