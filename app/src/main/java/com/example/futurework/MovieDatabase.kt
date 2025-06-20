// Reference:
// Portions of the following code were adapted from Lecture 8: Working with Databases,
// presented by Dr. Dimitris C. Dracopoulos, 5COSC023W - Mobile Application Development.
// Specifically, the setup and usage of the Room database (MovieDatabase, MovieDao, and
// Entity classes) were based on code examples shown in the lecture slides and demonstration code.
// Original material © University of Westminster.

package com.example.futurework

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database for movies with one table
@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDAO

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        // Gets the database
        // Uses the singleton pattern - only one database instance exists
        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create a new database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_db"  // Database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}