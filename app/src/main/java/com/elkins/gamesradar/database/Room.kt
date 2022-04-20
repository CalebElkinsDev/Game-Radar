package com.elkins.gamesradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface GamesDao {
    // Get all games
    @Query("SELECT * FROM databasegame")
    fun getGames(): LiveData<List<DatabaseGame>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // TODO update and replace if change
    fun insertAll(games: List<DatabaseGame>)
}

@TypeConverters(TypeConvertors::class)
@Database(entities = [DatabaseGame::class], version = 5, exportSchema = false)
abstract class GamesDatabase: RoomDatabase() {
    abstract val gamesDao: GamesDao
}

private lateinit var INSTANCE: GamesDatabase

/* Create or return singleton instance of database */
fun getDatabase(context: Context): GamesDatabase {
    synchronized(GamesDatabase::class.java) {
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                        GamesDatabase::class.java, "games")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}