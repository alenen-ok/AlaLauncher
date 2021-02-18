package space.alena.kominch.provider

import androidx.room.*


@Dao
interface HomeDao {
    @Query("SELECT * FROM ${HomeApp.TABLE_NAME} ORDER BY ${HomeApp.COLUMN_ID}")
    fun loadAllApps() : List<HomeApp>


    @Query("UPDATE ${HomeApp.TABLE_NAME} SET ${HomeApp.COLUMN_INDEX} = :count WHERE ${HomeApp.COLUMN_LABEL} LIKE :first AND " +
            "${HomeApp.COLUMN_NAME} LIKE :last")
    fun updateHomeApp(count: Int, first: String,last: String) : Int

    @Insert
    fun insertApp(app: HomeApp)

    @Update
    fun updateApp(app: HomeApp)

    @Query("DELETE FROM ${HomeApp.TABLE_NAME} WHERE ${HomeApp.COLUMN_INDEX} = :pos")
    fun deleteAppByPosition(pos: Int)

    @Delete
    fun deleteApp(app: HomeApp)

}