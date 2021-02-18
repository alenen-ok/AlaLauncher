package space.alena.contentprovidertolast

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val APPS_URI = Uri.parse("content://space.alena.kominch.provider/apps")
    private val APPS_LAST_URI = Uri.parse("content://space.alena.kominch.provider/last")

    private val COL_NAME = "name"
    private val COL_LABEL = "label"
    private val COL_COUNT = "count"
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.apps)
        findViewById<Button>(R.id.btn_showMax).setOnClickListener {
            try {
                requestApps(APPS_LAST_URI)
            }catch(ex: SecurityException) {
                textView.text = "Access to last used is protected"
            }
        }
        findViewById<Button>(R.id.btn_show).setOnClickListener {
            try {
                requestApps(APPS_URI)
            }catch(ex: SecurityException) {
                textView.text = "Access to all used records is protected"

            }
        }

    }
    private fun requestApps(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val stringBuilder = StringBuilder().append("Apps: ").append("\n")
        cursor?.use { _ ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                val label = cursor.getString(cursor.getColumnIndex(COL_LABEL))
                val count = cursor.getInt(cursor.getColumnIndex(COL_COUNT))

                stringBuilder.append("app: name = ").append(name).append("; ")
                        .append("label = ").append(label).append(";")
                        .append("count = ").append(count).append("\n").append("\n")
            }
            cursor.close()
        }
        textView.text = stringBuilder.toString()
    }
}
