package foocafe.org.foocafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import foocafe.org.foocafe.entities.AccessToken;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FooCafeLogin extends AppCompatActivity {

    private final String clientId = "285172c95c08767d1c4e4a6ace84a8daec545837aa13605ff159e3b387f40781";
    private final String clientSecret = "17003db694f8dff5c54ba9ae091e2afd7a03616c3ae6ba1c6fc1b4a9801ab349";
    private final String redirectUri = "your://redirecturi";
    private FooCafeAPI foocafeAPI;
    private String authCode;
    private AccessToken access;
    private String TAG = "TAG";

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foocafe_login);
        session = new Session(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (session.checkLogin()) {
            finish();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foocafeAPI = retrofit.create(FooCafeAPI.class);

        WebView w = findViewById(R.id.w2);
        w.getSettings().setJavaScriptEnabled(true);
        //  w.loadUrl("http://www.foocafe.org");
        w.loadUrl(ServiceGenerator.API_BASE_URL + "/oauth/authorize" + "?client_id=" + clientId + "&response_type=code" + "&redirect_uri=" + redirectUri);
        w.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("OAUTH", url);

                if (url != null && url.contains("?code=")) {
                    Log.i("OAUTH", url);

                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");

                    Log.i(TAG + "access", authCode);
                    AsyncCall task = new AsyncCall();           //does the Post to server
                    try {
                        task.execute().get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    session.createLogin(access.access_token, access.user_id);
                    Toast.makeText(FooCafeLogin.this, session.getAccessToken(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(i);
                    finish();
                } else if (url != null && url.contains("access_denied")) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
    }

    private class AsyncCall extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Call<AccessToken> token = foocafeAPI.getAccessToken(authCode, "authorization_code", clientId, clientSecret, redirectUri);

            try {
                access = token.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}