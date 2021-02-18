package space.alena.kominch.provider

import android.content.ContentValues
import android.provider.BaseColumns
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = HomeApp.TABLE_NAME)
class HomeApp {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    var id: Long = 0
    @ColumnInfo(name = COLUMN_LABEL)
    var label: String = ""
    @ColumnInfo(name = COLUMN_NAME)
    var name: String = ""
    @ColumnInfo(name = COLUMN_INDEX)
    var index_rv: Int = 0


    companion object {
        const val TABLE_NAME = "home"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_LABEL = "label"
        const val COLUMN_NAME = "name"
        const val COLUMN_INDEX = "index_rv"

        fun fromContentValues(@Nullable values: ContentValues?) : HomeApp{
            val app = HomeApp()
            if(values != null && values.containsKey(COLUMN_ID)){
                app.id = values.getAsLong(COLUMN_ID)
            }
            if(values != null && values.containsKey(COLUMN_LABEL)){
                app.label = values.getAsString(COLUMN_LABEL)
            }
            if(values != null && values.containsKey(COLUMN_NAME)){
                app.name = values.getAsString(COLUMN_NAME)
            }
            if(values != null && values.containsKey(COLUMN_INDEX)){
                app.index_rv = values.getAsInteger(COLUMN_INDEX)
            }
            return app
        }
    }
}