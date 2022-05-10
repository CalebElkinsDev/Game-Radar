package com.elkins.gamesradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface GamesDao {

    // Get Games with query
    @RawQuery(observedEntities = [DatabaseGame::class])
    fun getGames(query: SupportSQLiteQuery): LiveData<List<DatabaseGame>>

    @Query("SELECT * FROM databasegame WHERE id = :id")
    fun getGame(id: Long): DatabaseGame

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGame(game: DatabaseGame): Long

    /* Insert a list of games to the database that are not already present */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(games: List<DatabaseGame>)

    @Query("SELECT count(*) FROM databasegame")
    fun getDatabaseSize(): Long

    @Query("DELETE FROM databasegame")
    fun clearDatabase()

    @Update
    fun updateGame(game: DatabaseGame)

    @Transaction
    fun insertOrUpdateGame(game: DatabaseGame) {
        val id = insertGame(game)
        if(id == -1L) {
            val existingGame = getGame(game.id)

            val followingStatus = existingGame.following

            /**
             * Create a new DatabaseGame using the existing id and following status, but with
             * updated fields from the recently fetched game in case of changes since insertion.
            */
            val updatedGame = DatabaseGame(
                id = existingGame.id,
                guid = game.guid,
                name = game.name,
                imageUrl = game.imageUrl,
                platforms = game.platforms,
                originalReleaseDate = game.originalReleaseDate,
                expectedReleaseYear = game.expectedReleaseYear,
                expectedReleaseQuarter = game.expectedReleaseQuarter,
                expectedReleaseMonth = game.expectedReleaseMonth,
                expectedReleaseDay = game.expectedReleaseDay,
                releaseDateInMillis = game.releaseDateInMillis,
                following = followingStatus
            )

            // Update the game with modified and merged fields
            updateGame(updatedGame)
        }
    }
}

@TypeConverters(TypeConvertors::class)
@Database(entities = [DatabaseGame::class], version = 8, exportSchema = false)
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