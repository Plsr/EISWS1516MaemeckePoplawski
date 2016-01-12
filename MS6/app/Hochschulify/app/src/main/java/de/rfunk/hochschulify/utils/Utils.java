package de.rfunk.hochschulify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;

/**
 * Class with general helper methods.
 *
 * Created by chrispop on 03/12/15.
 */
public class Utils {

    // Server URL, stored here for global usage
    public static final String SERVER_URL = "http://10.0.2.2:3000";
    public static final String AUTH_PATH = "/auth";
    public static final String ENTRY_PATH = "/entries";
    public static final String USER_PATH = "/users";
    public static final String COURSE_PATH = "/courses";

    public static final String USERID_IDENTIFIER = "userid";
    public static final String PASSWORD_IDENTIFIER = "password";

    //TODO: Remove Password, only store login token
    public static final String LOGIN_USERNAME_KEY = "__USERNAME__";
    public static final String LOGIN_PASSWORD_KEY = "__PASSWORD__";
    public static final String LOGIN_AUTHTOKEN_KEY = "__AUTHTOKEN__";
    public static final String ACCOUNTTYPE_KEY = "__ACCOUNTTYPE__";


    /**
     * Saves a key-value-pair to the default shared preferences.
     *
     * Although there shouldn't be sent any empty strings to the function, it checks for it again.
     *
     * This function was taken from Stack Overflow and slightly improved
     * @see <a href="http://stackoverflow.com/a/19629701/4181679">Stack Overflow Thread</a>
     *
     * @param context Context of the calling method
     * @param key The key
     * @param value The Value
     * @return true if save was successful, false otherwise
     */
    public static boolean saveToSharedPrefs (Context context, String key, String value) {
        if(!isEmptyString(key) && !isEmptyString(value)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(key, value);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the value of a given key from the default shared memory.
     *
     * The defaultValue is specified by the caller to make checking against it mor convenient.
     *
     * NOTE: Only returns the defaultValue if the key does not exist.
     * If it exists but no value is set, the returned string is empty.
     *
     * This function was taken from Stack Overflow
     * @see <a href="http://stackoverflow.com/a/19629701/4181679">Stack Overflow Thread</a>
     *
     * @param context Context of the calling method
     * @param key The key
     * @param defaultValue defaultValue specified by the caller
     * @return The value matching the key or the defaultValue
     */
    public static String getFromSharedPrefs (Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Checks if there are values for a given Array of Strings.
     * Only returns true if all given keys do have a value.
     *
     * NOTE: Empty values are considered as non-existent by this method.
     *
     * @param context Context of the calling method
     * @param keys Array of keys to be checked
     * @return  true if there are valid values for all keys
     */
    public static boolean credentialsInSharedMem(Context context, String[] keys) {
        boolean checker = true;
        String defaultValue = "__DEFAULT__";
        for (String key : keys) {
            String prefVal = getFromSharedPrefs(context, key, defaultValue);
            if(isEmptyString(prefVal) || prefVal.equals(defaultValue)) {
                checker = false;
            }
        }
        return checker;
    }

    /**
     * Clears all keys and values from default shared preferences
     *
     * NOTE: This method was written for development and testing purposes only. Never use this
     * method in production.
     * Always remove single key & value pairs from the shared memory to keep control over what is deleted.
     *
     * @param context Context of the calling method
     */
    public static void clearSharedPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Helper method to test if a given string is empty
     *
     * This methods main purpose is to save space and improve readability.
     * Checks if the given String is null and checks it with the .isEmpty() function.
     *
     * @param string The String to test
     * @return true if String is empty
     */
    public static boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Checks if a given CharSequence is valid email Syntax.
     * NOTE: Only checks the Syntax, does NOT check the service for validation.
     *
     * Code was taken from Stack Overflow
     *
     * @see <a href="http://stackoverflow.com/a/9225678/4181679">Stack Overflow Thread</a>
     *
     * @param email eMail to be checked
     * @return true if syntax matches email syntax
     */
    public static boolean isValidEmailSyntax(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
