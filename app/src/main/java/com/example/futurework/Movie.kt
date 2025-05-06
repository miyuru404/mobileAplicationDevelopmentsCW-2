// Reference:
// Portions of the following code were adapted from Lecture 8: Working with Databases,
// presented by Dr. Dimitris C. Dracopoulos, 5COSC023W - Mobile Application Development.
// Specifically, the setup and usage of the Room database (MovieDatabase, MovieDao, and
// Entity classes) were based on code examples shown in the lecture slides and demonstration code.
// Original material © University of Westminster.


package com.example.futurework

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(

    @PrimaryKey val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String,

)

