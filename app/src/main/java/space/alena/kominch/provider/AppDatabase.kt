package space.alena.kominch.provider

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [App::class, HomeApp::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun homeDao(): HomeDao
}