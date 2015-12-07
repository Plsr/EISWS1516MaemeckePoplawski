package de.rfunk.hochschulify;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    private static final String EMAILINPUTID = "__EMAIL__";
    private static final String PASSWORDINPUTID = "__PASSWORD__";
    private static final String USERNAMEINPUTID = "__USERNAME__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, TextView> textViewMap = getTextViews();
                Map<String, TextInputLayout> inputLayoutMap = getTextInputLayouts();

                for(Map.Entry<String, TextView> entry : textViewMap.entrySet()) {
                    String textViewContent = entry.getValue().getText().toString();
                    if(Utils.isEmptyString(textViewContent)) {
                        String identifier = entry.getKey();
                        TextInputLayout layout = inputLayoutMap.get(identifier);
                        String errMessageEmpty = "Feld darf nicht leer sein";
                        displayErrorLabelOnTextInputLayout(layout, errMessageEmpty);
                    }
                }




            }
        });

    }

    public Map<String, TextView> getTextViews() {
        // Get all textviews
        TextView emailView = (TextView) findViewById(R.id.input_email);
        TextView usernameView = (TextView) findViewById(R.id.input_username);
        TextView passwordView = (TextView) findViewById(R.id.input_password);

        // Write Textviews to Hashmap
        Map<String, TextView> textViewMap = new HashMap<String, TextView>();
        textViewMap.put(EMAILINPUTID, emailView);
        textViewMap.put(USERNAMEINPUTID, usernameView);
        textViewMap.put(PASSWORDINPUTID, passwordView);

        return textViewMap;
    }

    public Map<String, TextInputLayout> getTextInputLayouts() {
        // Get all TextInputLayouts
        TextInputLayout emailInputLayout = (TextInputLayout) findViewById(R.id.layout_email);
        TextInputLayout usernameInputLayout = (TextInputLayout) findViewById(R.id.layout_username);
        TextInputLayout passwordInputLayout = (TextInputLayout) findViewById(R.id.layout_password);

        // Write TextInputlayouts to Hashmap
        Map<String, TextInputLayout> inputLayoutmap = new HashMap<String, TextInputLayout>();
        inputLayoutmap.put(EMAILINPUTID, emailInputLayout);
        inputLayoutmap.put(USERNAMEINPUTID, usernameInputLayout);
        inputLayoutmap.put(PASSWORDINPUTID, passwordInputLayout);
        return inputLayoutmap;
    }

    /**
     * Sets an error label with a provided error message for a TextInputLayout and gives the field focus
     * TODO: Duplicate form LoginActivity, write in Utils
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
