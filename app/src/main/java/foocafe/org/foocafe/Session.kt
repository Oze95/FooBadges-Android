package foocafe.org.foocafe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class Session internal constructor(private val context: Context) {

    private val sharedPreferences: SharedPreferences
    private val e: SharedPreferences.Editor

    private val isTwitterLoggedInAlready: Boolean
        get() = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false)

    internal val accessToken: String?
        get() = sharedPreferences.getString("accessToken", null)

    internal val uid: Int
        get() = sharedPreferences.getInt("user_id", 0)

    internal val isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean("isLoggedIn", false)

    init {
        sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)
        e = sharedPreferences.edit()
    }

    fun createLoginSession(token: String, secret: String, username: String) {
        // Storing login value as TRUE

        e.putString(PREF_KEY_OAUTH_TOKEN, token)
        e.putString(PREF_KEY_OAUTH_SECRET, secret)
        e.putString(UID, username)
        // Store login status - true
        e.putBoolean(PREF_KEY_TWITTER_LOGIN, true)
        e.commit() // save changes
    }

    internal fun checkLogin(): Boolean {
        // Check login status
        if (this.isTwitterLoggedInAlready) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(context, EventListActivity::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Staring Login Activity
            context.startActivity(i)

            return true
        }
        return false
    }

    internal fun logoutUser() {
        // Clearing all data from Shared Preferences
        e.clear()
        e.commit()
    }

    internal fun saveChapter(chapter: String) {
        e.putString(LAST_CHAPTER, chapter)
        e.commit()
    }

    internal fun checkChapter(): String {
        return sharedPreferences.getString(LAST_CHAPTER, "malmoe")
    }

    internal fun createLogin(accesstoken: String, user_id: Int) {

        e.putBoolean("isLoggedIn", true)
        e.putString("accessToken", accesstoken)
        e.putInt("user_id", user_id)
        e.commit()
    }

    companion object {
        private val PREF_KEY_OAUTH_TOKEN = "oauth_token"
        private val PREF_KEY_OAUTH_SECRET = "oauth_token_secret"
        private val PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn"
        private val UID = ""
        private val LAST_CHAPTER = "malmoe"
    }
}