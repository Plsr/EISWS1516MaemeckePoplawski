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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                // Remove all Error labels
                unsetAllErrorLabels(inputLayoutMap);

                //TODO: Write a function for this
                for (Map.Entry<String, TextView> entry : textViewMap.entrySet()) {
                    String identifier = entry.getKey();
                    String textViewContent = entry.getValue().getText().toString();
                    TextInputLayout layout = inputLayoutMap.get(identifier);
                    if (Utils.isEmptyString(textViewContent)) {
                        // TODO: Write constant in strings.xml
                        String errMessageEmpty = "Feld darf nicht leer sein";
                        displayErrorLabelOnTextInputLayout(layout, errMessageEmpty);
                    }
                    if (identifier.equals(EMAILINPUTID)) {
                        if (!Utils.isValidEmailSyntax(textViewContent) && !Utils.isEmptyString(textViewContent)) {
                            String errorMessageSyntax = "Bitte gültig email Adresse eingeben";
                            displayErrorLabelOnTextInputLayout(layout, errorMessageSyntax);
                        }
                    }
                    if (identifier.equals(USERNAMEINPUTID)) {
                        String regex = "[A-Za-z0-9//.//_//-]+";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(textViewContent);
                        if(textViewContent.length() > 12 && !Utils.isEmptyString(textViewContent)) {
                            String errUsernameLength = "Name darf max. 12 Zeichen lang sein.";
                            displayErrorLabelOnTextInputLayout(layout, errUsernameLength);
                        }
                        else if(!matcher.matches()) {
                            String forbiddenChar = "Nur Buchstaben, Zahlen und . - _ erlaubt";
                            displayErrorLabelOnTextInputLayout(layout, forbiddenChar);
                        }
                    }
                    if (identifier.equals(PASSWORDINPUTID)) {
                       if (textViewContent.length() < 6 && !Utils.isEmptyString(textViewContent)) {
                           String errPasswdLength = "Passwort muss mindestens 6 Zeichen lang sein";
                           displayErrorLabelOnTextInputLayout(layout, errPasswdLength);
                       }
                    }

                }


            }
        });

    }

    /**
     * Gets the TextViews in the Register form. Does NOT automatically detect all TextViews
     * in the Activity but needs to be updated by hand.
     * Returns the TextViews in a HashMap with static identifiers. These Identifiers are used Class-wide.
     *
     * @return HashMap with identifier key and TextView value pairs
     */
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

    /**
     * Gets the TextInputLayouts in the Register form. Does NOT automatically detect all TextInputLayouts
     * in the Activity but needs to be updated by hand.
     * Returns the TextInputLayoputs in a HashMap with static identifiers. These Identifiers are used Class-wide.
     *
     * @return HashMap with identifier key and TextInputLayout value pairs
     */
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
     * Unsets all error labels of TextInputLayouts in a given Hashmap.
     * If there is not an error label displayed at a label handed to this function,
     * notjing changes. There are now error thrown.
     *
     * @param layouts Hashmap with String and TextInputLayout key-value-pairs to
     *                be removed from error labels.
     */
    public void unsetAllErrorLabels(Map<String, TextInputLayout> layouts) {
        for(Map.Entry<String, TextInputLayout> entry: layouts.entrySet()) {
            entry.getValue().setErrorEnabled(false);
        }
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
