package foocafe.org.foocafe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class Splash : Activity() {

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.splash)

        val SPLASH_DISPLAY_LENGTH = 1000
        Handler().postDelayed({
            val mainIntent = Intent(this@Splash, EventListActivity::class.java)
            this@Splash.startActivity(mainIntent)
            this@Splash.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}