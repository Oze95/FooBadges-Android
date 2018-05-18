package foocafe.org.foocafe

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_CONTACTS

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private var exit: Boolean = false

    // UI references.
    private val mEmailView: AutoCompleteTextView? = null
    internal var ab: android.support.v7.app.ActionBar? = null

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.getItem(intent.getIntExtra("setCheckValue", 1)).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_events -> {
                    val k = Intent(applicationContext, EventListActivity::class.java)
                    k.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(k)
                    finish()
                }
            }
            true
        }

        val signInButton = findViewById<Button>(R.id.email_sign_in_button)
        signInButton.setOnClickListener {
            val i = Intent(applicationContext, FooCafeLogin::class.java)
            startActivity(i)
            finish()
        }

        val tool = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(tool)
        ab = supportActionBar
    }

    override fun onResume() {
        super.onResume()

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        val uri = intent.data
        if (uri != null && uri.toString().contains("?code=")) {
            Toast.makeText(this@LoginActivity, "Welcome back", Toast.LENGTH_LONG).show()
            Log.i("OAUTH", uri.toString())
            //  session.createLogin();
            // use the parameter your API exposes for the code (mostly it's "code")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                // get access token
                // we'll do that in a minute
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
            }
            val i = Intent(this, EventListActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return
        }
    }

    private fun mayRequestPermissions(): Boolean {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView!!, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { requestPermissions(arrayOf(READ_CONTACTS, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, INTERNET, BLUETOOTH_ADMIN, BLUETOOTH), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, INTERNET, BLUETOOTH_ADMIN, BLUETOOTH), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    override fun onBackPressed() {
        if (exit) {
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({ exit = false }, (2 * 1000).toLong())
        }
    }

    companion object {
        private val REQUEST_READ_CONTACTS = 0  // Behöver token för att kunna veta och spara att man är inloggad likt FooCafeLogin.
    }
}