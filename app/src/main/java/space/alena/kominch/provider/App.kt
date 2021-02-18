package space.alena.kominch.provider

import android.content.ContentValues
import android.provider.BaseColumns
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = App.TABLE_NAME)
class App {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    var id: Long = 0
    @ColumnInfo(name = COLUMN_LABEL)
    var label: String = ""
    @ColumnInfo(name = COLUMN_NAME)
    var name: String = ""
    @ColumnInfo(name = COLUMN_COUNT)
    var count: Int = 0
    @ColumnInfo(name = COLUMN_START)
    var start: Long = 0

    companion object {
        const val TABLE_NAME = "apps"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_LABEL = "label"
        const val COLUMN_NAME = "name"
        const val COLUMN_COUNT = "count"
        const val COLUMN_START = "start"

        fun fromContentValues(@Nullable values: ContentValues?) : App{
            val app = App()
            if(values != null && values.containsKey(COLUMN_ID)){
                app.id = values.getAsLong(COLUMN_ID)
            }
            if(values != null && values.containsKey(COLUMN_LABEL)){
                app.label = values.getAsString(COLUMN_LABEL)
            }
            if(values != null && values.containsKey(COLUMN_NAME)){
                app.name = values.getAsString(COLUMN_NAME)
            }
            if(values != null && values.containsKey(COLUMN_COUNT)){
                app.count = values.getAsInteger(COLUMN_COUNT)
            }
            if(values != null && values.containsKey(COLUMN_START)){
                app.start = values.getAsLong(COLUMN_START)
            }
            return app
        }
    }
}