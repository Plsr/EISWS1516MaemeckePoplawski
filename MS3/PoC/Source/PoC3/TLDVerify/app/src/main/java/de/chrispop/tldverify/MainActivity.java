package de.chrispop.tldverify;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    JSONArray jsonArray;
    String userMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText) findViewById(R.id.email_address);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                // No submit button, check if submit on keyboard is pressed
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    userMail = (String) editText.getText().toString();

                    // Debug
                    System.out.println(editText.getText().toString());

                    getJSON();
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void getJSON () {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://raw.githubusercontent.com/Plsr/EISWS1516MaemeckePoplawski/master/MS3/PoC/Source/PoC3/assets/tlds.json?token=ADxIRbbXfqK6aDF4H6pd9dE4-Jrfp7z4ks5WRlbbwA%3D%3D";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);
                            setResult();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public boolean checkTLD (String userMail) throws JSONException {
        String regEx = ".*@.*";
        for(int j = 0; j < jsonArray.length(); j++) {
            String st = regEx + Pattern.quote(jsonArray.get(j).toString());
            System.out.println(st);
            Pattern pattern = Pattern.compile(st);
            Matcher matcher = pattern.matcher(userMail);
            if(matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public void setResult () throws JSONException {
        TextView textView =  (TextView) findViewById(R.id.helpText);
        if (checkTLD(userMail)) {
            textView.setText("Validated");
        } else {
            textView.setText("NOPE");
        }
    }
}
