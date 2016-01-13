package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String AUTH_PATH = Utils.AUTH_PATH;

    // Credentials for Dummy User
    public static final String USERID = "56688d8040709bc9df7901ad";
    public static final String PASSWORD = "test";

    Toolbar toolbar;
    EditText identification;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setting up the Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        }

        // Setting up the OnClick listeners
        Button loginBtn = (Button) findViewById(R.id.button_login);
        Button registerBtn = (Button) findViewById(R.id.button_register);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performLogin(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }


    /**
     * Performs the login of a user if all data is entered correctly.
     *
     * Checks if all fields are populated and displays errors otherwise.
     * If both fields are populated, checks if the user credentials are valid and saves them to the shared preferences.
     * Afterwards, starts the HomeActivity.
     *
     * @param view current view
     */
    public void performLogin(View view) throws JSONException {
        // Get the layouts to display the material error labels
        final TextInputLayout identificationLayout = (TextInputLayout)findViewById(R.id.loginIdentification_input_layout);
        final TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.loginPassword_input_layout);

        identification = (EditText)findViewById(R.id.loginIdentification);
        password = (EditText)findViewById(R.id.loginPassword);

        final String identificationString = identification.getText().toString();
        String passwordString = password.getText().toString();

        // If both fields are populated, begin validation
        if(!Utils.isEmptyString(identificationString) && !Utils.isEmptyString(passwordString)) {
            // If one of the fields was empty before, remove the errors
            identificationLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);

            String url = Utils.SERVER_URL + Utils.AUTH_PATH;

            JSONObject body = new JSONObject();
            body.put("email", identificationString);
            body.put("password", passwordString);

            Req userAuth = new Req(LoginActivity.this, url);
            userAuth.post(body, new Req.Res() {
                @Override
                public void onSuccess(JSONObject res) {
                    System.out.println("YAY");
                    System.out.println(res);
                    if (res.has("auth_token")) {
                        try {
                            Utils.saveToSharedPrefs(LoginActivity.this, Utils.LOGIN_USERNAME_KEY, res.getString("_id"));
                            Utils.saveToSharedPrefs(LoginActivity.this, Utils.LOGIN_AUTHTOKEN_KEY, res.getString("auth_token"));
                            Utils.saveToSharedPrefs(LoginActivity.this, Utils.USER_KEY, res.toString());
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    System.out.println("OH NOES");
                    System.out.println(error.networkResponse.data.toString());


                }
            });
        }

        // If the identification field is empty, display an error
        if(Utils.isEmptyString(identificationString)) {
            String errorString = getString(R.string.err_identification_empty);
            displayErrorLabelOnTextInputLayout(identificationLayout, errorString);
        }

        // If the password field is empty, display an error
        if(Utils.isEmptyString(passwordString)) {
            String errorString = getString(R.string.err_password_empty);
            displayErrorLabelOnTextInputLayout(passwordLayout, errorString);
        }

    }


    /**
     * Sets an error label with a provided error message for a TextInputLayout and gives the field focus
     *
     * @param layout The Layout on which the error shall be displayed
     * @param errorMessage The error message to be displayed
     */
    private void displayErrorLabelOnTextInputLayout (TextInputLayout layout, String errorMessage) {
        layout.setError(errorMessage);
        if(layout.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}
