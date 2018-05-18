package foocafe.org.foocafe

import android.content.Intent
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast

import foocafe.org.foocafe.entities.BadgeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BadgesActivity : AppCompatActivity() {

    private var exit: Boolean = false
    private var foocafeAPI: FooCafeAPI? = null
    private var recyclerView: RecyclerView? = null
    private var aSwitch: Switch? = null
    private val TAG = "tag"

    private var session: Session? = null

    private var ab: android.support.v7.app.ActionBar? = null

    private var badgesCallback: Callback<BadgeList> = object : Callback<BadgeList> {
        override fun onResponse(call: Call<BadgeList>, response: Response<BadgeList>) {
            if (response.isSuccessful) {
                Log.i(TAG, "onResponse: ")
                val badgeList = response.body()
                recyclerView!!.adapter = BadgesAdapter(badgeList!!.badges!!, this@BadgesActivity)
            }
        }

        override fun onFailure(call: Call<BadgeList>, t: Throwable) {
            Log.i(TAG, "onFailure: ")
            t.printStackTrace()
        }
    }

    private var loadMyBadgesCallback: Callback<BadgeList> = object : Callback<BadgeList> {
        override fun onResponse(call: Call<BadgeList>, response: Response<BadgeList>) {
            if (response.isSuccessful) {
                Log.i(TAG, "onResponse: ")
                val badgeList = response.body()
                recyclerView!!.adapter = BadgesAdapter(badgeList!!.badges!!, this@BadgesActivity)
            }
        }

        override fun onFailure(call: Call<BadgeList>, t: Throwable) {
            Log.i(TAG, "onFailure: ")
            t.printStackTrace()
        }
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        session = Session(applicationContext)
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        recyclerView = findViewById(R.id.recycler_view)
        aSwitch = findViewById(R.id.switch1)

        val gridLayoutManager = GridLayoutManager(this, 4)
        recyclerView!!.layoutManager = gridLayoutManager
        setSupportActionBar(toolbar)
        ab = supportActionBar

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.getItem(2).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_checkIn -> if (!session!!.isLoggedIn) {
                    val i = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(applicationContext, CheckInActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    finish()
                }
                R.id.action_events -> {
                    val k = Intent(applicationContext, EventListActivity::class.java)
                    k.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(k)
                    finish()
                }
            }
            true
        }

        aSwitch!!.setOnClickListener {
            if (aSwitch!!.isChecked) {
                foocafeAPI!!.loadMyBadges(session!!.uid).enqueue(loadMyBadgesCallback)
            } else {
                foocafeAPI!!.loadBadges().enqueue(badgesCallback)
            }
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        foocafeAPI = retrofit.create(FooCafeAPI::class.java)
        foocafeAPI!!.loadBadges().enqueue(badgesCallback)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {

                val handlerClose = Handler()
                handlerClose.postDelayed({
                    session!!.logoutUser()
                    Toast.makeText(applicationContext, "Logged out", Toast.LENGTH_SHORT).show()
                    val i = Intent(applicationContext, EventListActivity::class.java)
                    startActivity(i)
                    finish()
                    overridePendingTransition(0, 0)
                }, 100)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}