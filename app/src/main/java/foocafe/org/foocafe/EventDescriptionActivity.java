package foocafe.org.foocafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static foocafe.org.foocafe.EventListActivity.PREFERENCES;

public class EventDescriptionActivity extends AppCompatActivity {

    private WebView w;
    private BottomNavigationView bottomNavigationView;
    Session session;
    private Toolbar tool;
    private Button button;
    private boolean signedUp;
    private TextView t2;
    private String desc, title, subtitle, location, date, time;
    private android.support.v7.app.ActionBar ab = null;

    private FooCafeAPI foocafeAPI;
    private String cache;
    private String cacheLocation;
    private SharedPreferences preferences;

    Callback<SignUpCredential> SignUpCallback = new Callback<SignUpCredential>() {
        @Override
        public void onResponse(Call<SignUpCredential> call, Response<SignUpCredential> response) {
            if (response.isSuccessful()) {
                signedUp = true;
                Toast.makeText(getApplicationContext(), "Successful sign up", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<SignUpCredential> call, Throwable t) {
            t.printStackTrace();
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(EventDescriptionActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Failed to sign up. Try again!");
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        Bundle bundle = getIntent().getExtras();
        desc = bundle.getString("desc");
        title = bundle.getString("title");
        subtitle = bundle.getString("subtitle");
        date = bundle.getString("date");
        time = bundle.getString("time");

        tool = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(tool);
        ab = getSupportActionBar();

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        cacheLocation = preferences.getString("chapter", "Malmö");

        Intent inte = getIntent();
        cache = inte.getStringExtra("list");
        session = new Session(getApplicationContext());

        w = (WebView) findViewById(R.id.WebView);
        button = (Button) findViewById(R.id.button);
        t2 = (TextView) findViewById(R.id.textView5);
        t2.setText(subtitle + "\n" + title + "\n" + time + " " + date);

        w.getSettings().setJavaScriptEnabled(true);
        String result = desc.replace("\n", "<br>");
        result = result.replace("\r", "<br>");
        w.loadDataWithBaseURL("http://www.foocafe.org", result, "text/html", "UTF-8", "about:blank");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
                                Intent k = new Intent(getApplicationContext(), EventListActivity.class);
                                k.putExtra("list", cache);
                                startActivity(k);
                                finish();
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                i.putExtra("url", getIntent().getStringExtra("url"));
                startActivity(i);

              /*  if(!session.isLoggedIn()){
                    Intent i = new Intent(getApplicationContext(),FooCafeLogin.class);
                    startActivity(i);

                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                    alertDialogBuilder.setMessage("Do you want to sign up to this event?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if(!signedUp) {
                                        // Innan vi skickar en POST måste vi veta att personen är inloggad eller låta personen logga in först och sen skicka tillbaka till denna sida och signa up till ett event.
                                        signUpCredential = new SignUpCredential(session.getUID(), title);
                                        foocafeAPI.signUp(signUpCredential, location, title).enqueue(SignUpCallback);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Again?", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
*/
            }
        });


        switch (cacheLocation) {
            case "Malmö":
                location = "malmoe";
                break;
            case "Stockholm":
                location = "stockholm";
                break;
            case "Copenhagen":
                location = "copenhagen";
                break;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foocafeAPI = retrofit.create(FooCafeAPI.class);


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), EventListActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}