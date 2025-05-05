package com.example.futurework

import android.app.Activity
import android.content.Context
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
    val context = LocalContext.current
    var movieTitle by remember { mutableStateOf("") }
    var retrievedMovie by remember { mutableStateOf<Movie?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ){
        Column {
            TextBox(movieTitle) { movieTitle = it }

            Spacer(modifier = Modifier.height(10.dp))
            RetrieveMovie(movieTitle, context) { retrievedMovie = it }
            Spacer(modifier = Modifier.height(10.dp))
            SaveMovie(retrievedMovie)
            Spacer(modifier = Modifier.height(30.dp))
            if (retrievedMovie != null) {
                DisplayMovieData(movie = retrievedMovie!!)
            }


        }
    }

}

@Composable
fun TextBox(movieTitle: String, onTextChange: (String) -> Unit) {

    OutlinedTextField(
        value = movieTitle,
        onValueChange = onTextChange,
        label = { Text("Enter your name") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun RetrieveMovie(movieTitle: String, context: Context, onMovieFetched: (Movie?) -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {

            coroutineScope.launch {
                fetchMovieData(context, movieTitle) { onMovieFetched(it) }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB2BDE2),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth() // makes the button take full horizontal space
            .height(60.dp), // makes the button taller
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 20.dp, // creates the shadow
            pressedElevation = 12.dp // deeper shadow when pressed
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Retrieve Movie")
            Spacer(modifier = Modifier.weight(1f)) // Pushes icon to the end
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "retrieve"
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun SaveMovie(movie: Movie?) {
    val context = LocalContext.current

    Button(
        onClick = {
            insertMovie(context, movie)
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


fun fetchMovieData(context: Context, title: String, onResult: (Movie?) -> Unit) {
    Thread {
        try {

            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val apiKey = "c582a2b0"
            val url = URL("https://www.omdbapi.com/?t=$encodedTitle&apikey=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Read the API response
            val result = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            Log.d("MovieFetch", "API Response: $result")

            val json = JSONObject(result)

            // Check the Response value from the API
            if (json.getString("Response") == "True") {
                val movie = Movie(
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

                // Pass the movie data back to the UI thread
                (context as Activity).runOnUiThread {
                    onResult(movie)
                }
            } else {
                // If the API response is "False", show the error message
                val errorMessage = json.getString("Error")
                Log.e("MovieFetch", "API Error: $errorMessage")
                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MovieFetch", "Exception: ${e.localizedMessage}") // Log the exception message

            //  show a toast message
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Error: Unable to fetch movie data", Toast.LENGTH_SHORT).show()
            }
        }
    }.start()
}


fun insertMovie(context: Context, movie: Movie?) {
    if (movie != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                "movies_db"
            ).build()

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
fun DisplayMovieData(movie: Movie) {
    Column {
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
    }
}









