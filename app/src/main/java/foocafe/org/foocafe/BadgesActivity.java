package foocafe.org.foocafe;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import foocafe.org.foocafe.entities.BadgeList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BadgesActivity extends AppCompatActivity {

    private boolean exit;
    private FooCafeAPI foocafeAPI;
    private RecyclerView recyclerView;
    private Switch aSwitch;
    private String TAG = "tag";

    private Session session;

    android.support.v7.app.ActionBar ab = null;

    Callback<BadgeList> badgesCallback = new Callback<BadgeList>() {
        @Override
        public void onResponse(@NonNull Call<BadgeList> call, @NonNull Response<BadgeList> response) {
            if (response.isSuccessful()) {
                Log.i(TAG, "onResponse: ");
                BadgeList badgeList = response.body();
                recyclerView.setAdapter(new BadgesAdapter(badgeList.badges, BadgesActivity.this));

            }
        }

        @Override
        public void onFailure(@NonNull Call<BadgeList> call, @NonNull Throwable t) {
            Log.i(TAG, "onFailure: ");
            t.printStackTrace();

        }
    };

    Callback<BadgeList> loadMyBadgesCallback = new Callback<BadgeList>() {
        @Override
        public void onResponse(@NonNull Call<BadgeList> call, @NonNull Response<BadgeList> response) {
            if (response.isSuccessful()) {
                Log.i(TAG, "onResponse: ");
                BadgeList badgeList = response.body();
                recyclerView.setAdapter(new BadgesAdapter(badgeList.badges, BadgesActivity.this));

            }
        }

        @Override
        public void onFailure(@NonNull Call<BadgeList> call, @NonNull Throwable t) {
            Log.i(TAG, "onFailure: ");
            t.printStackTrace();

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
        setContentView(R.layout.activity_badges);

        session = new Session(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        aSwitch = findViewById(R.id.switch1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_checkIn:
                                if (!session.isLoggedIn()) {
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                    break;
                                } else {
                                    Intent i = new Intent(getApplicationContext(), CheckInActivity.class);
                                    //i.putExtra("list", cache);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                    break;
                                }
                            case R.id.action_events:
                                Intent k = new Intent(getApplicationContext(), EventListActivity.class);
                                //      k.putExtra("list",cache);
                                k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(k);
                                finish();
                                break;
                            case R.id.action_badges:
                                break;
                        }
                        return true;
                    }
                });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (aSwitch.isChecked()) {
                    foocafeAPI.loadMyBadges(session.getUID()).enqueue(loadMyBadgesCallback);
                } else {
                    foocafeAPI.loadBadges().enqueue(badgesCallback);

                }
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        foocafeAPI = retrofit.create(FooCafeAPI.class);
        foocafeAPI.loadBadges().enqueue(badgesCallback);


    }

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

        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
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
}