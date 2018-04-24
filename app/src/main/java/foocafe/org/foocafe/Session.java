package foocafe.org.foocafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Session {
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    private static final String UID = "";
    private static final String LAST_CHAPTER = "malmoe";

    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor e;

    Session(Context context) {
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

    boolean checkLogin() {
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

    void logoutUser() {
        // Clearing all data from Shared Preferences
        e.clear();
        e.commit();
    }

    void saveChapter(String chapter) {
        e.putString(LAST_CHAPTER, chapter);
        e.commit();
    }

    String checkChapter() {
        return sharedPreferences.getString(LAST_CHAPTER, "malmoe");
    }


    void createLogin(String accesstoken, int user_id) {

        e.putBoolean("isLoggedIn", true);
        e.putString("accessToken", accesstoken);
        e.putInt("user_id", user_id);
        e.commit();
    }

    String getAccessToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    int getUID() {
        return sharedPreferences.getInt("user_id", 0);
    }

    boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
