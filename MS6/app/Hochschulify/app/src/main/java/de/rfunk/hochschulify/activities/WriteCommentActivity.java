package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.R;

public class WriteCommentActivity extends AppCompatActivity {

    TextView answerTo;

    String parentAuthor;
    Bundle intentExtras;
    JSONObject mParentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        intentExtras = getIntent().getExtras();
        String jsonString = intentExtras.getString("parent");
        try {
            mParentEntry = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parentAuthor = mParentEntry.getJSONObject("user").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Antwort schreiben");
        setSupportActionBar(toolbar);

        answerTo = (TextView) findViewById(R.id.answer_to);
        answerTo.setText("Antwort an " + parentAuthor + ":");


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
