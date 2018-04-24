package foocafe.org.foocafe;

import android.content.DialogInterface;
import android.content.Intent;
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

import foocafe.org.foocafe.entities.SignUpCredential;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDescriptionActivity extends AppCompatActivity {

    Session session;
    private String desc, title, subtitle, date, time;
    private String cache;

    Callback<SignUpCredential> SignUpCallback = new Callback<SignUpCredential>() {
        @Override
        public void onResponse(@NonNull Call<SignUpCredential> call, @NonNull Response<SignUpCredential> response) {
            if (response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Successful sign up", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<SignUpCredential> call, @NonNull Throwable t) {
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
        if (bundle != null) {
            desc = bundle.getString("desc");
            title = bundle.getString("title");
            subtitle = bundle.getString("subtitle");
            date = bundle.getString("date");
            time = bundle.getString("time");
        }
        Toolbar tool = findViewById(R.id.my_toolbar);
        setSupportActionBar(tool);

        Intent inte = getIntent();
        cache = inte.getStringExtra("list");
        session = new Session(getApplicationContext());

        WebView w = findViewById(R.id.WebView);
        Button button = findViewById(R.id.button);
        TextView t2 = findViewById(R.id.textView5);
        t2.setText(String.format("%s\n%s\n%s %s", subtitle, title, time, date));

        w.getSettings().setJavaScriptEnabled(true);
        String result = desc.replace("\n", "<br>");
        result = result.replace("\r", "<br>");
        w.loadDataWithBaseURL("http://www.foocafe.org", result, "text/html", "UTF-8", "about:blank");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
            }
        });
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