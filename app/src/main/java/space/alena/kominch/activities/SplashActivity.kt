package space.alena.kominch.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import space.alena.kominch.R

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ApplicationListActivity::class.java))
        finish()
    }
}
