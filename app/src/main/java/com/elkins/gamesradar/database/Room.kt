package com.elkins.gamesradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface GamesDao {
    // Get all games
    @Query("SELECT * FROM databasegame")
    fun getGames(): LiveData<List<DatabaseGame>>

    /* Insert a list of games to the database that are not already present */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(games: List<DatabaseGame>)

    @Query("DELETE FROM databasegame")
    fun clearDatabase()
}

@TypeConverters(TypeConvertors::class)
@Database(entities = [DatabaseGame::class], version = 6, exportSchema = false)
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