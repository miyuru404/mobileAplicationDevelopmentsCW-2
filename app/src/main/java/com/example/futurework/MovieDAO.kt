package com.example.futurework

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE LOWER(actors) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchMoviesByActor(query: String): List<Movie>

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

}
