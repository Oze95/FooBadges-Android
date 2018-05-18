package foocafe.org.foocafe

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import foocafe.org.foocafe.entities.Event
import foocafe.org.foocafe.entities.EventList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.INTERNET
import android.content.Context
import foocafe.org.foocafe.R.id.textView3

class EventListActivity : AppCompatActivity() {

    private var foocafeAPI: FooCafeAPI? = null
    private var session: Session? = null
    private var recyclerView: RecyclerView? = null
    private var chaptersSpinner: Spinner? = null
    private var ab: android.support.v7.app.ActionBar? = null
    private var DB: TinyDB? = null
    private var cache: String? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var date: String? = null
    private var textView: TextView? = null
    private var exit: Boolean = false

    internal var eventsCallback: Callback<EventList> = object : Callback<EventList> {
        override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
            if (response.isSuccessful) {
                val eventList = response.body()
                var list: ArrayList<Event>? = ArrayList(0)
                val filterlist = eventList!!.events!!.filterTo(ArrayList()) { it.date == date }

                list!!.addAll(eventList.events!!)

                if (list.size == 0) {
                    textView!!.text = getString(R.string.no_events_in_near_future)
                } else {
                    textView!!.text = getString(R.string.events)
                }

                if (list.size <= 4) {
                    recyclerView!!.overScrollMode = View.OVER_SCROLL_NEVER
                }

                recyclerView!!.adapter = EventListAdapter(eventList.events!!, this@EventListActivity)
                DB!!.putListObject(cache, eventList.events)
                list = DB!!.getListObject(session!!.uid.toString() + cache!!, Event::class.java)
                if (DB!!.getListObject(session!!.uid.toString() + cache!!, Event::class.java) == null || filterlist.size == 0 || list == null || list.size == 0 || list[0].date != date) {
                    DB!!.putListObject(session!!.uid.toString() + cache!!, filterlist)
                }
            }
        }

        override fun onFailure(call: Call<EventList>, t: Throwable) {
            t.printStackTrace()
            val eventList = EventList()
            eventList.events = DB!!.getListObject(cache, Event::class.java)
            recyclerView!!.adapter = EventListAdapter(eventList.events!!, this@EventListActivity)
        }
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventlist)
        getPermissions()

        DB = TinyDB(this)
        val tool = findViewById<Toolbar>(R.id.my_toolbar)
        session = Session(applicationContext)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        date = dateFormat.format(Date())
        textView = findViewById(textView3)

        Log.i("SuperDate", date)

        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        editor = preferences!!.edit()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView!!.menu.getItem(1).isChecked = true

        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_checkIn -> if (!session!!.isLoggedIn) {
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
                R.id.action_badges -> if (!session!!.isLoggedIn) {
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

        chaptersSpinner = findViewById(R.id.chaptersSpinner)

        chaptersSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                when (chaptersSpinner!!.selectedItem.toString()) {
                    "Malmö" -> {
                        cache = "Malmö"
                        foocafeAPI!!.loadEvents("malmoe").enqueue(eventsCallback)
                        session!!.saveChapter("malmoe")
                        editor!!.putString("chapter", "Malmö")
                    }
                    "Stockholm" -> {
                        cache = "Stockholm"
                        foocafeAPI!!.loadEvents("stockholm").enqueue(eventsCallback)
                        session!!.saveChapter("stockholm")
                        editor!!.putString("chapter", "Stockholm")
                    }
                    "Copenhagen" -> {
                        cache = "Copenhagen"
                        foocafeAPI!!.loadEvents("copenhagen").enqueue(eventsCallback)
                        session!!.saveChapter("copenhagen")
                        editor!!.putString("chapter", "Copenhagen")
                    }
                }
                editor!!.apply()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        foocafeAPI = retrofit.create(FooCafeAPI::class.java)

        val myAdapter = ArrayAdapter(this@EventListActivity,
                android.R.layout.simple_expandable_list_item_1, resources.getStringArray(R.array.chapters))
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chaptersSpinner!!.adapter = myAdapter

        val chapter = session!!.checkChapter()

        chaptersSpinner!!.setSelection(myAdapter.getPosition(chapter))
        when (chapter) {
            "malmoe" -> chaptersSpinner!!.setSelection(0)
            "stockholm" -> chaptersSpinner!!.setSelection(1)
            "copenhagen" -> chaptersSpinner!!.setSelection(2)
        }

        setSupportActionBar(tool)
        ab = supportActionBar

        recyclerView = findViewById(R.id.recycler_view)
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView!!.layoutManager = gridLayoutManager
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
        return if (session!!.isLoggedIn) {
            menuInflater.inflate(R.menu.settings, menu)
            true
        } else {
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val handlerClose = Handler()
                handlerClose.postDelayed({
                    session!!.logoutUser()
                    val i = Intent(applicationContext, EventListActivity::class.java)
                    startActivity(i)
                    finish()
                    overridePendingTransition(0, 0)
                }, 100)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getPermissions() {
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, INTERNET, BLUETOOTH_ADMIN, BLUETOOTH), 0)
        }
    }

    companion object {
        val PREFERENCES = "MyPrefs"
    }
}