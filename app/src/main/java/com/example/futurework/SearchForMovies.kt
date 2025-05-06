package com.example.futurework

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay


class SearchForMovies : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecondScreen()
        }
    }
}


@Composable
fun SecondScreen() {
    // Track search input and results
    var movieTitle by rememberSaveable { mutableStateOf("") }
    var retrievedMovie by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ) {
        Column {
            TextBox(movieTitle) { movieTitle = it }
            Spacer(modifier = Modifier.height(10.dp))

            if (isPortrait) {
                // Portrait layout - stack components vertically
                RetrieveMovie(movieTitle,false) { retrievedMovie = it }
                Spacer(modifier = Modifier.height(10.dp))
                SaveMovie(retrievedMovie)
                Spacer(modifier = Modifier.height(30.dp))
                DisplayAllMovies(retrievedMovie)
            } else {
                // Landscape layout - side by side components
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        RetrieveMovie(movieTitle,false) { retrievedMovie = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        SaveMovie(retrievedMovie)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(2f)) {
                        DisplayAllMovies(retrievedMovie)
                    }
                }
            }
        }
    }
}

// Text input field for movie searches
@Composable
fun TextBox(movieTitle: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = movieTitle,
        onValueChange = onTextChange,
        label = { Text("search for movies") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

// reusable Button to search for movies from API
// this function can search for either one movie or multipart movies with similar name
// making this function can used for both
@Composable
fun RetrieveMovie(movieTitle: String, allMovies:Boolean, onMovieFetched: (List<Movie>) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Button(
        onClick = {
            coroutineScope.launch {
                if (movieTitle.isBlank()) {
                    Toast.makeText(context, "Please enter a movie title", Toast.LENGTH_SHORT).show()
                } else {
                    if (!allMovies) {
                        // search for single movie
                        val movies = fetchMovieData(movieTitle)
                        onMovieFetched(movies)
                        delay(10000) // Add delay to handle network delay
                        if (movies.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "No movies found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Search for all matching movies
                        val movies = fetchAllMovieData(movieTitle)
                        onMovieFetched(movies)
                        delay(10000) // Add delay to handle network delay
                        if (movies.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "No movies found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB2BDE2),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 20.dp,
            pressedElevation = 12.dp
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Retrieve Movie")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "retrieve"
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}


// Button to save movie results to local database
@Composable
fun SaveMovie(movieList: List<Movie>) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (movieList.isEmpty()) {
                Toast.makeText(context, "No movie to save!", Toast.LENGTH_SHORT).show()
            } else {
                // Save movies in background thread
                CoroutineScope(Dispatchers.IO).launch {
                    val db = MovieDatabase.getInstance(context)
                    movieList.forEach { db.movieDao().insertMovie(it) }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Movies saved to DB!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB2BDE2),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 20.dp,
            pressedElevation = 12.dp
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Save movie to Database")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "save"
            )
        }
    }
}


// Fetch movie data from OMDB API
// this function will get one movie data that similar to user input
suspend fun fetchMovieData(title: String): List<Movie> {
    return withContext(Dispatchers.IO) {
        try {
            // Encode title and set up API request
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val apiKey = "c582a2b0"
            val url = URL("https://www.omdbapi.com/?t=$encodedTitle&apikey=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Read response and parse JSON
            val result = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val json = JSONObject(result)

            if (json.getString("Response") == "True") {
                listOf(
                    Movie(
                        title = json.getString("Title"),
                        year = json.getString("Year"),
                        rated = json.getString("Rated"),
                        released = json.getString("Released"),
                        runtime = json.getString("Runtime"),
                        genre = json.getString("Genre"),
                        director = json.getString("Director"),
                        writer = json.getString("Writer"),
                        actors = json.getString("Actors"),
                        plot = json.getString("Plot")
                    )
                )
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MovieFetch", "Error fetching data", e)
            emptyList()
        }
    }
}

// Display movie details in a scrollable list
@Composable
fun DisplayAllMovies(movies: List<Movie>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())//make it scrollable
            .padding(16.dp)
    ) {
        movies.forEach { movie ->
            // Show movie details
            Text(text = "Title: ${movie.title}")
            Text(text = "Year: ${movie.year}")
            Text(text = "Rated: ${movie.rated}")
            Text(text = "Released: ${movie.released}")
            Text(text = "Runtime: ${movie.runtime}")
            Text(text = "Genre: ${movie.genre}")
            Text(text = "Director: ${movie.director}")
            Text(text = "Writer: ${movie.writer}")
            Text(text = "Actors: ${movie.actors}")
            Text(text = "Plot: ${movie.plot}")
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}