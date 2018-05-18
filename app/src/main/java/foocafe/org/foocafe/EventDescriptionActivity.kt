package foocafe.org.foocafe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import foocafe.org.foocafe.entities.SignUpCredential
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDescriptionActivity : AppCompatActivity() {

    private lateinit var session: Session
    private var desc: String? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var date: String? = null
    private var time: String? = null
    private var cache: String? = null

    internal var SignUpCallback: Callback<SignUpCredential> = object : Callback<SignUpCredential> {
        override fun onResponse(call: Call<SignUpCredential>, response: Response<SignUpCredential>) {
            if (response.isSuccessful) {
                Toast.makeText(applicationContext, "Successful sign up", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<SignUpCredential>, t: Throwable) {
            t.printStackTrace()
            val alertDialog = android.support.v7.app.AlertDialog.Builder(this@EventDescriptionActivity).create()
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Failed to sign up. Try again!")
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_description)

        val bundle = intent.extras
        if (bundle != null) {
            desc = bundle.getString("desc")
            title = bundle.getString("title")
            subtitle = bundle.getString("subtitle")
            date = bundle.getString("date")
            time = bundle.getString("time")
        }
        val tool = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(tool)

        val inte = intent
        cache = inte.getStringExtra("list")
        session = Session(applicationContext)

        val w = findViewById<WebView>(R.id.WebView)
        val button = findViewById<Button>(R.id.button)
        val t2 = findViewById<TextView>(R.id.textView5)
        t2.text = String.format("%s\n%s\n%s %s", subtitle, title, time, date)

        w.settings.javaScriptEnabled = true
        var result = desc!!.replace("\n", "<br>")
        result = result.replace("\r", "<br>")
        w.loadDataWithBaseURL("http://www.foocafe.org", result, "text/html", "UTF-8", "about:blank")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.getItem(1).isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_checkIn -> if (!session.isLoggedIn) {
                    val i = Intent(applicationContext, LoginActivity::class.java)
                    i.putExtra("setCheckValue", 0)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(applicationContext, CheckInActivity::class.java)
                    i.putExtra("list", cache)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    finish()
                }
                R.id.action_events -> {
                    val k = Intent(applicationContext, EventListActivity::class.java)
                    k.putExtra("list", cache)
                    startActivity(k)
                    finish()
                }
                R.id.action_badges -> if (!session.isLoggedIn) {
                    val i = Intent(applicationContext, LoginActivity::class.java)
                    i.putExtra("setCheckValue", 2)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(applicationContext, BadgesActivity::class.java)
                    i.putExtra("list", cache)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    finish()
                }
            }
            true
        }

        button.setOnClickListener {
            val i = Intent(applicationContext, SignUpActivity::class.java)
            i.putExtra("url", intent.getStringExtra("url"))
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        val i = Intent(applicationContext, EventListActivity::class.java)
        startActivity(i)
        finish()
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}