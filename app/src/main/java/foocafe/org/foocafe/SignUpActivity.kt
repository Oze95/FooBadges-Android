package foocafe.org.foocafe

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val eventUrl = intent.getStringExtra("url")

        val web = findViewById<WebView>(R.id.webView)
        web.settings.javaScriptEnabled = true
        web.loadUrl(eventUrl + "/registrations/new")
        web.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                if (url != eventUrl + "/registrations/new") {
                    Toast.makeText(this@SignUpActivity, "Redirecting", Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({ finish() }, 2500)
                }
            }
        }
    }
}