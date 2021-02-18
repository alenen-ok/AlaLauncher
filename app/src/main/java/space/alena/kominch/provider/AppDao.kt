package space.alena.kominch.provider

import android.database.Cursor
import androidx.room.*

@Dao
interface AppDao {

    @Query("SELECT COUNT(*) FROM ${App.TABLE_NAME}")
    fun count() : Int

    @Query("SELECT * FROM ${App.TABLE_NAME}")
    fun getAll() : List<App>

    @Query("SELECT * FROM ${App.TABLE_NAME}")
    fun selectAll() : Cursor

    @Query("SELECT * FROM ${App.TABLE_NAME} WHERE ${App.COLUMN_COUNT} NOT LIKE 0")
    fun selectAllUsed() : Cursor

    @Query("SELECT * FROM ${App.TABLE_NAME} WHERE ${App.COLUMN_ID} = :id")
    fun selectById(id: Long): Cursor

    @Query("SELECT * FROM ${App.TABLE_NAME} WHERE ${App.COLUMN_START} NOT LIKE 0 AND " +
            "${App.COLUMN_START} LIKE (SELECT MAX(${App.COLUMN_START}) FROM ${App.TABLE_NAME})")
    fun selectByArg(): Cursor

    @Insert
    fun insert(app: App?): Long

    @Insert
    fun insertAll(vararg  app: App)

    @Query("SELECT * FROM ${App.TABLE_NAME} WHERE ${App.COLUMN_LABEL} LIKE :first AND " +
            "${App.COLUMN_NAME} LIKE :last LIMIT 1")
    fun findByLabelAndName(first: String, last: String) : App

    @Query("UPDATE ${App.TABLE_NAME} SET ${App.COLUMN_COUNT} = :count, ${App.COLUMN_START} = :time WHERE ${App.COLUMN_LABEL} LIKE :first AND " +
            "${App.COLUMN_NAME} LIKE :last")
    fun updateApp(count: Int, first: String,last: String, time: Long) : Int

    @Update
    fun update(app: App): Int

    @Delete
    fun delete(app: App)

    @Query("DELETE FROM ${App.TABLE_NAME} WHERE ${App.COLUMN_ID} = :id")
    fun deleteById(id: Long) : Int

}