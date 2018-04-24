package foocafe.org.foocafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Session {
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    static final String UID = "";
    static final String LAST_CHAPTER = "malmoe";

    Context context;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor e;

    // Constructor
    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        e = sharedPreferences.edit();
    }

    public void createLoginSession(String token, String secret, String username) {
        // Storing login value as TRUE

        e.putString(PREF_KEY_OAUTH_TOKEN, token);
        e.putString(PREF_KEY_OAUTH_SECRET, secret);
        e.putString(UID, username);
        // Store login status - true
        e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
        e.commit(); // save changes
    }

    private boolean isTwitterLoggedInAlready() {
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public boolean checkLogin() {
        // Check login status
        if (this.isTwitterLoggedInAlready()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, EventListActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);

            return true;
        }
        return false;
    }

    ;

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        e.clear();
        e.commit();

        // After logout redirect user to Loing Activity
        //Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // Staring Login Activity
        // context.startActivity(i);
    }

    public void saveChapter(String chapter) {
        //e.remove(LAST_CHAPTER);
        e.putString(LAST_CHAPTER, chapter);
        e.commit();
    }

    public String checkChapter() {
        return sharedPreferences.getString(LAST_CHAPTER, "malmoe");
    }


    public void createLogin(String accesstoken, int user_id) {

        e.putBoolean("isLoggedIn", true);
        e.putString("accessToken", accesstoken);
        e.putInt("user_id", user_id);
        e.commit();

    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    public int getUID() {
        return sharedPreferences.getInt("user_id", 0);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
