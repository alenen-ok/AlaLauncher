package space.alena.kominch.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_profile.toolbar
import kotlinx.android.synthetic.main.content_profile.*
import space.alena.kominch.R
import space.alena.kominch.adapters.CustomListViewAdapter

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
    override fun onStart() {
        YandexMetrica.reportEvent("Show profile")
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        val textView = findViewById<TextView>(R.id.text_from_sp)
        textView.text = prefs.getString("silent_push", "No text")
        listView.adapter = CustomListViewAdapter(this, R.layout.activity_profile)
        listView.onItemClickListener = AdapterView.OnItemClickListener(){ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            if(i == 0 || i == 1){
                YandexMetrica.reportEvent("Click on user phone")
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+375445827329"))
                startActivity(callIntent)
            }
            if(i == 2){
                YandexMetrica.reportEvent("Click user mail")
                val mailIntent = Intent(Intent.ACTION_SEND).apply {
                    // The intent does not have a URI, so declare the "text/plain" MIME type
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("alena_kominch@mail.ru")) // recipients
                }
                startActivity(mailIntent)
            }
            if(i == 3){
                val mailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("alena_work@mail.ru")) // recipients
                }
                startActivity(mailIntent)
            }
            if (i == 4 || i == 5){
                YandexMetrica.reportEvent("Click user address")
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:53.89217986780948,27.567268125356627?z=17"))
                startActivity(mapIntent)
            }
            if(i == 6){
                YandexMetrica.reportEvent("Click user instagram")
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/aaaaaaaaaaaaaaaaaaaaaaa____aaa/"))
                startActivity(webIntent)

            }
            if( i == 7){
                YandexMetrica.reportEvent("Click user gitlab")
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gitlab.com/alenakominch"))
                startActivity(webIntent)
            }
        }
        super.onStart()
    }

}
