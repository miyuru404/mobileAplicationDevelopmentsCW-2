package com.example.futurework

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Interface for database operations on movies
@Dao
interface MovieDAO {

    // Adds a single movie to the database
    // If movie already exists, it gets replaced
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    // Finds movies that have a specific actor in them
    // The search is case-insensitive
    @Query("SELECT * FROM movies WHERE LOWER(actors) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchMoviesByActor(query: String): List<Movie>

    // Gets all movies from the database
    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    // Adds multiple movies at once
    // Replaces any that already exist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    // Searches movies by title
    // Case-insensitive search that finds partial matches too
    @Query("SELECT * FROM movies WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchAllMoviesByTitle(query: String): List<Movie>

}