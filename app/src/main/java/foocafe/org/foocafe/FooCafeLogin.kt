package foocafe.org.foocafe

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import java.io.IOException
import java.util.concurrent.ExecutionException

import foocafe.org.foocafe.entities.AccessToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FooCafeLogin : AppCompatActivity() {

    private val clientId = "285172c95c08767d1c4e4a6ace84a8daec545837aa13605ff159e3b387f40781"
    private val clientSecret = "17003db694f8dff5c54ba9ae091e2afd7a03616c3ae6ba1c6fc1b4a9801ab349"
    private val redirectUri = "your://redirecturi"
    private var foocafeAPI: FooCafeAPI? = null
    private var authCode: String? = null
    private var access: AccessToken? = null
    private val TAG = "TAG"

    private var session: Session? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_foocafe_login)
        session = Session(applicationContext)
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        if (session!!.checkLogin()) {
            finish()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(FooCafeAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        foocafeAPI = retrofit.create(FooCafeAPI::class.java)

        val w = findViewById<WebView>(R.id.w2)
        w.settings.javaScriptEnabled = true
        //  w.loadUrl("http://www.foocafe.org");
        w.loadUrl(ServiceGenerator.API_BASE_URL + "/oauth/authorize" + "?client_id=" + clientId + "&response_type=code" + "&redirect_uri=" + redirectUri)
        w.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String?) {
                Log.i("OAUTH", url)

                if (url != null && url.contains("?code=")) {
                    Log.i("OAUTH", url)

                    val uri = Uri.parse(url)
                    authCode = uri.getQueryParameter("code")

                    Log.i(TAG + "access", authCode)
                    val task = AsyncCall()           //does the Post to server
                    try {
                        task.execute().get()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }

                    session!!.createLogin(access!!.access_token!!, access!!.user_id)
                    Toast.makeText(this@FooCafeLogin, session!!.accessToken, Toast.LENGTH_SHORT).show()
                    val i = Intent(applicationContext, EventListActivity::class.java)
                    startActivity(i)
                    finish()
                } else if (url != null && url.contains("access_denied")) {
                    val i = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    0)
        }
    }

    private inner class AsyncCall : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            val token = foocafeAPI!!.getAccessToken(authCode!!, "authorization_code", clientId, clientSecret, redirectUri)

            try {
                access = token.execute().body()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}