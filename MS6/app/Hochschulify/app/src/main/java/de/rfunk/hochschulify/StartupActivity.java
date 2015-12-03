package de.rfunk.hochschulify;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartupActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText identification;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

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
     * Checks the input fields of the login for correctness.
     * For now, it just checks if one of the values equals "falsch" and displays the error label then,
     * for demonstration purposes.
     *
     * @param view current view
     */
    public void performLogin(View view) {
        // Get the layouts to display the material error labels
        TextInputLayout identificationLayout = (TextInputLayout)findViewById(R.id.loginIdentification_input_layout);
        TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.loginPassword_input_layout);

        identification = (EditText)findViewById(R.id.loginIdentification);
        password = (EditText)findViewById(R.id.loginPassword);

        if(identification.getText().toString().equals("falsch"))  {
            identificationLayout.setError(getString(R.string.err_msg_identification));
        }
        else if (password.getText().toString().equals("falsch")) {
                passwordLayout.setError(getString(R.string.err_msg_password));
        }
        else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

    }


}
