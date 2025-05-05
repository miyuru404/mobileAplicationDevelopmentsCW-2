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

    var movieTitle by rememberSaveable { mutableStateOf("") }
    //var retrievedMovie by remember { mutableStateOf<Movie?>(null) }
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

                RetrieveMovie(movieTitle) { retrievedMovie = it }
                Spacer(modifier = Modifier.height(10.dp))
                SaveMovie(retrievedMovie)
                Spacer(modifier = Modifier.height(30.dp))
                if (retrievedMovie != null) {
                    DisplayMovieData(movie = retrievedMovie!!)
                }
            } else {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        RetrieveMovie(movieTitle) { retrievedMovie = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        SaveMovie(retrievedMovie)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(2f)) {
                        if (retrievedMovie != null) {
                            DisplayMovieData(movie = retrievedMovie!!)
                        }
                    }
                }
            }
        }
    }
}


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

@Composable
fun RetrieveMovie(movieTitle: String, onMovieFetched: (List<Movie>) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                val movies = fetchMovieData(movieTitle)
                onMovieFetched(movies)
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




@Composable
fun SaveMovie(movieList: List<Movie>) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (movieList.isEmpty()) {
                Toast.makeText(context, "No movie to save!", Toast.LENGTH_SHORT).show()
            } else {
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



suspend fun fetchMovieData(title: String): List<Movie> {
    return withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val apiKey = "c582a2b0"
            val url = URL("https://www.omdbapi.com/?t=$encodedTitle&apikey=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

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




fun insertMovie(context: Context, movie: Movie?) {
    if (movie != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = MovieDatabase.getInstance(context)
            db.movieDao().insertMovie(movie)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Movie saved to DB!", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "No movie to save!", Toast.LENGTH_SHORT).show()
    }
}
@Composable
fun DisplayMovieData(movie: List<Movie>) {
    Column {
        movie.forEach {
            Text(text = "Title: ${it.title}")
            Text(text = "Year: ${it.year}")
            Text(text = "Rated: ${it.rated}")
            Text(text = "Released: ${it.released}")
            Text(text = "Runtime: ${it.runtime}")
            Text(text = "Genre: ${it.genre}")
            Text(text = "Director: ${it.director}")
            Text(text = "Writer: ${it.writer}")
            Text(text = "Actors: ${it.actors}")
            Text(text = "Plot: ${it.plot}")
            Spacer(modifier = Modifier.height(16.dp)) // Space between movies
        }
    }
}











