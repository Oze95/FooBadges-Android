package foocafe.org.foocafe;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import static foocafe.org.foocafe.EventListActivity.PREFERENCES;

public class CheckInActivity extends AppCompatActivity implements BeaconConsumer {
    private String TAG = "CheckInTag";
    private Location loc;
    private Location target;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Session session;
    private CheckInCredential checkInCredential;
    private BeaconManager beaconManager;
    private Region reg;
    private boolean beacon;
    private String location;
    private String beaconID = null;
    private ArrayList<Event> list = new ArrayList<>();
    private String date;
    private boolean checkInSuccessful;
    private String eventID;
    private Toolbar tool;
    private boolean exit;
    android.support.v7.app.ActionBar ab = null;

    private TextView t;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private TinyDB db;
    private String cache;
    private SharedPreferences preferences;

    private GridLayoutManager gridLayoutManager ;
    private FooCafeAPI foocafeAPI;
    private String uniqueID;
    private EventListAdapter adapter;
    private String beaconIdentifier ="0x03676f6f2e676c2f417131387a46";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uniqueID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        session = new Session(getApplicationContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        cache = preferences.getString("chapter", "Malmö");

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i(TAG, "LOCATION CHANGED");
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        tool = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(tool);
        ab = getSupportActionBar();

        //Custom ACtionBar and some ActionBar actions
        t = (TextView) findViewById(R.id.textView);
        t.setText("Events today");
        db = new TinyDB(this);
        list = db.getListObject(session.getUID()+cache,Event.class);

        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (list.size()==0) {
            t.setText("No events today");
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } else if (list.size() == 1) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(260,360,100,100);
            params.height=750;
            params.width=550;
            recyclerView.setLayoutParams(params);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        } else if (list.size() == 2) {
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        adapter = new EventListAdapter(list, CheckInActivity.this);
        recyclerView.setAdapter(adapter);
        beacon = false;
        target = new Location("FOO");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_checkIn:
                                break;
                            case R.id.action_events:
                                Intent k = new Intent(getApplicationContext(), EventListActivity.class);
                                k.putExtra("list",cache);
                                k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(k);
                                finish();
                                break;
                            case R.id.action_badges:
                                Intent i = new Intent(getApplicationContext(),BadgesActivity.class);
                                startActivity(i);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
        switch (cache) {
            case "Malmö":
                target.setLongitude(12.991607);
                target.setLatitude(55.612453);
                session.saveChapter("malmoe");
                location = "malmoe";
                break;
            case "Stockholm":
                target.setLongitude(18.075951);
                target.setLatitude(59.307116);
                session.saveChapter("stockholm");
                location = "stockholm";
                break;
            case "Copenhagen":
                target.setLongitude(12.559258);
                target.setLatitude(55.667367);
                session.saveChapter("copenhagen");
                location = "copenhagen";
                break;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foocafeAPI = retrofit.create(FooCafeAPI.class);

        beacon = false;
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //         setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.bind(this);
        reg = new Region("myRangingUniqueId", null, null, null);
        session = new Session(getApplicationContext());

    }
    public String getUIDcache(){
        return session.getUID()+cache;
    }

    public boolean checkin(){
        startLocationUpdates();
        BluetoothAdapter blueToothEnabled = BluetoothAdapter.getDefaultAdapter();

        Log.i("SuperClick", Integer.toString(db.getAll().size()));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "No permissions");
        }
        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (loc != null && loc.distanceTo(target) < 2000 && beacon ) {

            stopLocationUpdates();    // Turn off the updates after Checkin, else it will drain battery real quick ^^

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            int userID = 1;//Integer.parseInt(session.getUID()); // Ska ersättas med token eller liknande som vi inte har än
            checkInCredential = new CheckInCredential(userID, beaconID, uniqueID, timestamp);
            Log.i(TAG, String.valueOf(checkInCredential.getUserID()));
            Log.i(TAG, checkInCredential.getDeviceID());
            Log.i(TAG, checkInCredential.getTimestamp());
           // Log.i(TAG, checkInCredential.getBeaconID());  // Will fix when we uses beacon
            Log.i(TAG, adapter.getEventID());

            AsyncCall task = new AsyncCall();           //does the Post to server
            try {
                task.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            beacon =false;

            if(checkInSuccessful){
                Toast.makeText(getApplicationContext(), "Check In Success", Toast.LENGTH_SHORT).show();
                return true;
            }

        } else if (loc == null) {
            Toast.makeText(getApplicationContext(), "Please enable your WIFI/GPS", Toast.LENGTH_SHORT).show();
            return false;
        }  else if (loc.distanceTo(target) > 2000){
            Toast.makeText(getApplicationContext(), "You are not at a Foo Café", Toast.LENGTH_SHORT).show();
            return false;
        }   else if (!blueToothEnabled.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please enable your Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }else if( !beacon||beaconID != null){
            Toast.makeText(getApplicationContext(), "Contact Foo café regarding beacon failure", Toast.LENGTH_SHORT).show();
            return false;
        }
        //  TODO: Check signup - if not singup
        //  TODO: Check for GPS - kollar gps koordinater
        //  TODO: Check for beacon - kollar beacon i

        return false;
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            beaconManager.startRangingBeaconsInRegion(reg);
            Log.i(TAG, "BEAOCN SERVICE");
        } catch (RemoteException e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        try {
            beaconManager.stopRangingBeaconsInRegion(reg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "startloc");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }

    protected void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }
    protected void onStart() {
        super.onStart();
    }
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i(TAG, "Bluetooth");
                if (beacons.size() > 0) {
                    Log.i(TAG, "Finns beacon");
                    for(Beacon b : beacons){
                        String url = UrlBeaconUrlCompressor.uncompress(b.getId1().toByteArray());
                        Log.i(TAG, url);
                        Log.i(TAG, b.toString());
                        Log.i(TAG, b.getId1().toString());
                        beaconID=url;
                        if (beaconIdentifier.equals(b.getId1().toString())) {
                            Log.i(TAG, "WE DONE");
                            beaconID=url;
                            beacon = true;
                            try {
                                beaconManager.stopRangingBeaconsInRegion(reg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(reg);
            Log.i(TAG, "BEAOCN SERVICE");
        } catch (RemoteException e) {
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:

                Handler handlerClose = new Handler();
                handlerClose.postDelayed(new Runnable() {
                    public void run() {
                        session.logoutUser();
                        Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), EventListActivity.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);
                    }
                },100);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncCall extends AsyncTask <Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Call<CheckInCredential> sendPost = foocafeAPI.checkIn(checkInCredential, location, adapter.getEventID());
            try {
                if(sendPost.execute().isSuccessful()) {
                 checkInSuccessful = true;
                } else{
                    checkInSuccessful=false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void res){

            if(!checkInSuccessful){
                AlertDialog alertDialog = new AlertDialog.Builder(CheckInActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Failed to check in. Try again!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

    }
}