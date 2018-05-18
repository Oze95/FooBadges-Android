package foocafe.org.foocafe

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import foocafe.org.foocafe.EventListActivity.Companion.PREFERENCES

import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.concurrent.ExecutionException

import foocafe.org.foocafe.entities.CheckInCredential
import foocafe.org.foocafe.entities.Event
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CheckInActivity : AppCompatActivity(), BeaconConsumer {
    private val TAG = "CheckInTag"
    private var loc: Location? = null
    private var target: Location? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private lateinit var session: Session
    private var checkInCredential: CheckInCredential? = null
    private var beaconManager: BeaconManager? = null
    private var reg: Region? = null
    private var beacon: Boolean = false
    private var location: String? = null
    private var beaconID: String? = null
    private var list = ArrayList<Event>()
    private var checkInSuccessful: Boolean = false
    private var tool: Toolbar? = null
    private var exit: Boolean = false
    private var ab: android.support.v7.app.ActionBar? = null

    private var t: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var db: TinyDB? = null
    private var cache: String? = null
    private var preferences: SharedPreferences? = null

    private var gridLayoutManager: GridLayoutManager? = null
    private var foocafeAPI: FooCafeAPI? = null
    private var uniqueID: String? = null
    private var adapter: EventListAdapter? = null
    private val beaconIdentifier = "0x03676f6f2e676c2f417131387a46"

    val uiDcache: String
        get() = session.uid.toString() + cache!!

    override fun onCreate(savedInstanceState: Bundle?) {
        uniqueID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        session = Session(applicationContext)
        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        cache = preferences!!.getString("chapter", "Malmö")

        // Acquire a reference to the system Location Manager
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Define a listener that responds to location updates
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the network location provider.
                Log.i(TAG, "LOCATION CHANGED")
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        tool = findViewById(R.id.my_toolbar)
        setSupportActionBar(tool)
        ab = supportActionBar

        t = findViewById(R.id.textView)
        db = TinyDB(this)
        list = db!!.getListObject(session.uid.toString() + cache!!, Event::class.java)

        gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.recycler)
        recyclerView!!.layoutManager = gridLayoutManager

        if (list.size == 0) {
            t!!.text = getString(R.string.no_events_today)
        }
        if (list.size < 5) {
            recyclerView!!.overScrollMode = View.OVER_SCROLL_NEVER
        }
        adapter = EventListAdapter(list, this@CheckInActivity)
        recyclerView!!.adapter = adapter
        beacon = false
        target = Location("FOO")

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView!!.menu.getItem(0).isChecked = true
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_events -> {
                    val k = Intent(applicationContext, EventListActivity::class.java)
                    k.putExtra("list", cache)
                    k.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(k)
                    finish()
                }
                R.id.action_badges -> {
                    val i = Intent(applicationContext, BadgesActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
            true
        }
        when (cache) {
            "Malmö" -> {
                target!!.longitude = 12.991607
                target!!.latitude = 55.612453
                session.saveChapter("malmoe")
                location = "malmoe"
            }
            "Stockholm" -> {
                target!!.longitude = 18.075951
                target!!.latitude = 59.307116
                session.saveChapter("stockholm")
                location = "stockholm"
            }
            "Copenhagen" -> {
                target!!.longitude = 12.559258
                target!!.latitude = 55.667367
                session.saveChapter("copenhagen")
                location = "copenhagen"
            }
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        foocafeAPI = retrofit.create(FooCafeAPI::class.java)

        beacon = false
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this)
        beaconManager!!.beaconParsers.clear()
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //         setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        // Detect the telemetry (TLM) frame:
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT))
        // Detect the URL frame:
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT))
        beaconManager!!.bind(this)
        reg = Region("myRangingUniqueId", null, null, null)
        session = Session(applicationContext)
    }

    fun checkin(): Boolean {
        startLocationUpdates()
        val blueToothEnabled = BluetoothAdapter.getDefaultAdapter()

        Log.i("SuperClick", Integer.toString(db!!.all.size))
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "No permissions")
        }
        loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (loc != null && loc!!.distanceTo(target) < 2000 && beacon) {

            stopLocationUpdates()    // Turn off the updates after Checkin, else it will drain battery real quick ^^

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timestamp = dateFormat.format(Date())
            val userID = 1//Integer.parseInt(session.getUID()); // Ska ersättas med token eller liknande som vi inte har än
            checkInCredential = CheckInCredential(userID, beaconID!!, uniqueID!!, timestamp)
            Log.i(TAG, checkInCredential!!.userID.toString())
            Log.i(TAG, checkInCredential!!.deviceID)
            Log.i(TAG, checkInCredential!!.timestamp)
            // Log.i(TAG, checkInCredential.getBeaconID());  // Will fix when we uses beacon
            Log.i(TAG, adapter!!.eventID)

            val task = AsyncCall()           //does the Post to server
            try {
                task.execute().get()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            beacon = false

            if (checkInSuccessful) {
                Toast.makeText(applicationContext, "Check In Success", Toast.LENGTH_SHORT).show()
                return true
            }

        } else if (loc == null) {
            Toast.makeText(applicationContext, "Please enable your WIFI/GPS", Toast.LENGTH_SHORT).show()
            return false
        } else if (loc!!.distanceTo(target) > 2000) {
            Toast.makeText(applicationContext, "You are not at a Foo Café", Toast.LENGTH_SHORT).show()
            return false
        } else if (!blueToothEnabled.isEnabled) {
            Toast.makeText(applicationContext, "Please enable your Bluetooth", Toast.LENGTH_SHORT).show()
            return false
        } else if (!beacon || beaconID != null) {
            Toast.makeText(applicationContext, "Contact Foo café regarding beacon failure", Toast.LENGTH_SHORT).show()
            return false
        }
        return false
    }

    override fun onBackPressed() {
        if (exit) {
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            beaconManager!!.startRangingBeaconsInRegion(reg!!)
            Log.i(TAG, "BEACON SERVICE")
        } catch (ignored: RemoteException) {
        }
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
        try {
            beaconManager!!.stopRangingBeaconsInRegion(reg!!)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "startloc")
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        }
    }

    private fun stopLocationUpdates() {
        locationManager!!.removeUpdates(locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager!!.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.addRangeNotifier { beacons, region ->
            Log.i(TAG, "Bluetooth")
            if (beacons.isNotEmpty()) {
                Log.i(TAG, "Finns beacon")
                for (b in beacons) {
                    val url = UrlBeaconUrlCompressor.uncompress(b.id1.toByteArray())
                    Log.i(TAG, url)
                    Log.i(TAG, b.toString())
                    Log.i(TAG, b.id1.toString())
                    beaconID = url
                    if (beaconIdentifier == b.id1.toString()) {
                        Log.i(TAG, "WE DONE")
                        beaconID = url
                        beacon = true
                        try {
                            beaconManager!!.stopRangingBeaconsInRegion(reg!!)
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        try {
            beaconManager!!.startRangingBeaconsInRegion(reg!!)
            Log.i(TAG, "BEACON SERVICE")
        } catch (ignored: RemoteException) {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val handlerClose = Handler()
                handlerClose.postDelayed({
                    session.logoutUser()
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

    private inner class AsyncCall : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {

            val sendPost = foocafeAPI!!.checkIn(checkInCredential!!, location!!, adapter!!.eventID!!)
            try {
                checkInSuccessful = sendPost.execute().isSuccessful
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(res: Void) {
            if (!checkInSuccessful) {
                val alertDialog = AlertDialog.Builder(this@CheckInActivity).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Failed to check in. Try again!")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }
    }
}