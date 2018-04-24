package foocafe.org.foocafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.INTERNET;
import static foocafe.org.foocafe.R.id.textView3;

public class EventListActivity extends AppCompatActivity {
    public static final String PREFERENCES = "MyPrefs";

    private FooCafeAPI foocafeAPI;
    private Session session;
    private RecyclerView recyclerView;
    private Spinner chaptersSpinner;
    android.support.v7.app.ActionBar ab = null;
    private TinyDB DB;
    private String cache;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String date;
    private TextView textView;
    private boolean exit;

    Callback<EventList> eventsCallback = new Callback<EventList>() {
        @Override
        public void onResponse(Call<EventList> call, Response<EventList> response) {
            if (response.isSuccessful()) {
                EventList eventList = response.body();
                ArrayList<Event> filterlist = new ArrayList<>();
                ArrayList<Event> list = new ArrayList<>(0);
                for (Event e : eventList.events) {
                    if (e.date.equals(date)) {
                        filterlist.add(e);
                    }
                }

                list.addAll(eventList.events);

                if (list.size() == 0) {
                    textView.setText(getString(R.string.no_events_in_near_future));
                } else {
                    textView.setText(getString(R.string.events));
                }

                if (list.size() <= 4) {
                    recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }

                recyclerView.setAdapter(new EventListAdapter(eventList.events, EventListActivity.this));
                DB.putListObject(cache, eventList.events);
                list = DB.getListObject(session.getUID() + cache, Event.class);
                if (DB.getListObject(session.getUID() + cache, Event.class) == null || filterlist.size() == 0 || list == null || list.size() == 0 || !(list.get(0).date.equals(date))) {
                    DB.putListObject(session.getUID() + cache, filterlist);
                }
            }
        }

        @Override
        public void onFailure(Call<EventList> call, Throwable t) {
            t.printStackTrace();
            EventList eventList = new EventList();
            eventList.events = DB.getListObject(cache, Event.class);
            recyclerView.setAdapter(new EventListAdapter(eventList.events, EventListActivity.this));
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventlist);
        getPermissions();

        DB = new TinyDB(this);
        Toolbar tool = findViewById(R.id.my_toolbar);
        session = new Session(getApplicationContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());
        textView = findViewById(textView3);

        Log.i("SuperDate", date);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_checkIn:
                                if (!session.isLoggedIn()) {
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    i.putExtra("setCheckValue", 0);
                                    startActivity(i);
                                    finish();
                                    break;
                                } else {
                                    Intent i = new Intent(getApplicationContext(), CheckInActivity.class);
                                    i.putExtra("list", cache);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                    break;
                                }
                            case R.id.action_events:
                                break;
                            case R.id.action_badges:
                                if (!session.isLoggedIn()) {
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    i.putExtra("setCheckValue", 2);
                                    startActivity(i);
                                    finish();
                                    break;
                                } else {
                                    Intent i = new Intent(getApplicationContext(), BadgesActivity.class);
                                    i.putExtra("list", cache);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                    break;
                                }
                        }
                        return true;
                    }
                });

        chaptersSpinner = findViewById(R.id.chaptersSpinner);

        chaptersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (chaptersSpinner.getSelectedItem().toString()) {
                    case "Malmö":
                        cache = "Malmö";
                        foocafeAPI.loadEvents("malmoe").enqueue(eventsCallback);
                        session.saveChapter("malmoe");
                        editor.putString("chapter", "Malmö");
                        break;
                    case "Stockholm":
                        cache = "Stockholm";
                        foocafeAPI.loadEvents("stockholm").enqueue(eventsCallback);
                        session.saveChapter("stockholm");
                        editor.putString("chapter", "Stockholm");
                        break;
                    case "Copenhagen":
                        cache = "Copenhagen";
                        foocafeAPI.loadEvents("copenhagen").enqueue(eventsCallback);
                        session.saveChapter("copenhagen");
                        editor.putString("chapter", "Copenhagen");
                        break;
                }
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        foocafeAPI = retrofit.create(FooCafeAPI.class);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(EventListActivity.this,
                android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.chapters));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chaptersSpinner.setAdapter(myAdapter);

        String chapter = session.checkChapter();

        chaptersSpinner.setSelection(myAdapter.getPosition(chapter));
        switch (chapter) {
            case "malmoe":
                cache = "Malmö";
                foocafeAPI.loadEvents("malmoe").enqueue(eventsCallback);
                session.saveChapter("malmoe");
                chaptersSpinner.setSelection(0);
                break;
            case "stockholm":
                cache = "Stockholm";
                foocafeAPI.loadEvents("stockholm").enqueue(eventsCallback);
                session.saveChapter("stockholm");
                chaptersSpinner.setSelection(1);
                break;
            case "copenhagen":
                cache = "Copenhagen";
                foocafeAPI.loadEvents("copenhagen").enqueue(eventsCallback);
                session.saveChapter("copenhagen");
                chaptersSpinner.setSelection(2);
                break;
        }

        setSupportActionBar(tool);
        ab = getSupportActionBar();

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
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
            }, 2 * 1000);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        if (session.isLoggedIn()) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.settings, menu);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Handler handlerClose = new Handler();
                handlerClose.postDelayed(new Runnable() {
                    public void run() {
                        session.logoutUser();
                        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), EventListActivity.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }, 100);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPermissions() {
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, INTERNET, BLUETOOTH_ADMIN, BLUETOOTH}, 0);
        }
    }
}