package space.alena.kominch.provider

import android.appwidget.AppWidgetProvider
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import androidx.annotation.Nullable
import androidx.room.Room
import com.yandex.metrica.YandexMetrica
import java.util.concurrent.Callable


class MyContentProvider : ContentProvider() {

    companion object{
        private const val APPS = 100
        private const val APPS_LAST = 101

        val AUTHORITY = "space.alena.kominch.provider"

        private val URI_MATCHER: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            URI_MATCHER.addURI(AUTHORITY, "apps", APPS)
            URI_MATCHER.addURI(AUTHORITY, "last", APPS_LAST)
        }
    }

    @Nullable
    override fun insert(uri: Uri, @Nullable values: ContentValues?): Uri? = null

    @Nullable
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        YandexMetrica.reportEvent("Someone got access to query at content provider")
        if(context == null) return null
        val db = Room.databaseBuilder(
                context!!,
                AppDatabase::class.java, "kominch.db"
        ).build()
        return when (URI_MATCHER.match(uri)) {
            APPS -> {
                db.appDao().selectAllUsed()
            }
            APPS_LAST -> {
                db.appDao().selectByArg()
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun onCreate(): Boolean = true

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        YandexMetrica.reportEvent("Someone got access to update at content provider")
        /*when(URI_MATCHER.match(uri)){
            APPS -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            APPS_ID -> {
                if(context == null) return 0
                val app = App.fromContentValues(values)
                app.id = ContentUris.parseId(uri)
                val count = myDB.appDao().update(app)
                context!!.contentResolver.notifyChange(uri, null)
                return count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }*/
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        YandexMetrica.reportEvent("Someone got access to delete at content provider")
        /*when(URI_MATCHER.match(uri)){
            APPS -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            APPS_ID -> {
                if(context == null) return 0
                val count : Int = myDB.appDao().deleteById(ContentUris.parseId(uri))
                context!!.contentResolver.notifyChange(uri, null)
                return count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }*/
        return 0
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            APPS -> ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.$AUTHORITY.provider_apps"
            APPS_LAST -> ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.$AUTHORITY.provider_last"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
}