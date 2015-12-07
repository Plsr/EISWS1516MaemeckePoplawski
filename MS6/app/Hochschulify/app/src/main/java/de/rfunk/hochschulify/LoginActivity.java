package de.rfunk.hochschulify;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

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
                performLogin(v);
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
    public void performLogin(View view) {
        // Get the layouts to display the material error labels
        TextInputLayout identificationLayout = (TextInputLayout)findViewById(R.id.loginIdentification_input_layout);
        TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.loginPassword_input_layout);

        identification = (EditText)findViewById(R.id.loginIdentification);
        password = (EditText)findViewById(R.id.loginPassword);

        String identificationString = identification.getText().toString();
        String passwordString = password.getText().toString();

        // If both fields are populated, begin validation
        if(!Utils.isEmptyString(identificationString) && !Utils.isEmptyString(passwordString)) {
            // If one of the fields was empty before, remove the errors
            identificationLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);

            // Check if identificationString is valid email syntax
            if(!Utils.isValidEmailSyntax(identificationString)) {
                // TODO: Only check for Usernames on validation
            }

            //TODO: Check if identification and password are valid

            // Clearing shared Preferences for testing reasons
            //TODO: Remove
            Utils.clearSharedPreferences(this);

            // Assuming identification and password are correct, we're storing those in
            // the shared Preferences.
            // TODO: Don't store the password, store a login token generated by the server
            // TODO: Check if save was successful?
            Utils.saveToSharedPrefs(this, Utils.LOGIN_USERNAME_KEY, identificationString);
            Utils.saveToSharedPrefs(this, Utils.LOGIN_PASSWORD_KEY, passwordString);

            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeIntent);
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
