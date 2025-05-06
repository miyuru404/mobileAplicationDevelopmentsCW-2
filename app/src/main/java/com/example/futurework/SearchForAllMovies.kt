package com.example.futurework

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.futurework.ui.theme.FutureWorkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class SerchForAllMovies : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForthScreen()
        }
    }
}


@Composable
fun ForthScreen() {
    // State variables to track user input and search results
    var movieTitle by rememberSaveable { mutableStateOf("") }
    var retrievedMovie by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }

    // Get the current device configuration to determine orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ) {
        Column {
            TextBox3(movieTitle) { movieTitle = it }
            Spacer(modifier = Modifier.height(10.dp))

            // Adapt layout based on device orientation
            if (isPortrait) {
                // Portrait layout
                RetrieveMovie(movieTitle, true) { retrievedMovie = it }
                Spacer(modifier = Modifier.height(10.dp))
                Spacer(modifier = Modifier.height(30.dp))
                DisplayAllMovies(retrievedMovie)
            } else {
                // Landscape layout
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        RetrieveMovie(movieTitle, true) { retrievedMovie = it }
                        Spacer(modifier = Modifier.height(10.dp))
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


//Composable function that creates a text input field for entering movie search queries.
@Composable
fun TextBox3(movieName: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = movieName,
        onValueChange = onTextChange,
        label = { Text("Search for any movies") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}


//  Fetches all movies from the OMDB API based on user's search query.
// this will provide all similar movies to the input text

suspend fun fetchAllMovieData(query: String): List<Movie> {
    return withContext(Dispatchers.IO) {
        val movies = mutableListOf<Movie>()
        try {
            // Encode query for URL and create API request
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val apiKey = "c582a2b0" // API key for OMDB access
            val url = URL("https://www.omdbapi.com/?s=$encodedQuery&apikey=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Parse API response
            val result = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val json = JSONObject(result)

            // If movies were found, process them
            if (json.getString("Response") == "True") {
                val searchArray = json.getJSONArray("Search")

                // For each movie in search results, fetch detailed information
                for (i in 0 until searchArray.length()) {
                    val item = searchArray.getJSONObject(i)
                    val movieTitle = item.getString("Title")

                    // Make a second API call to get detailed movie information
                    val detailUrl = URL("https://www.omdbapi.com/?t=${URLEncoder.encode(movieTitle, "UTF-8")}&apikey=$apiKey")
                    val detailConnection = detailUrl.openConnection() as HttpURLConnection
                    val detailResult = detailConnection.inputStream.bufferedReader().readText()
                    detailConnection.disconnect()

                    val detailJson = JSONObject(detailResult)
                    if (detailJson.getString("Response") == "True") {
                        // Create Movie object with detailed information and add to list
                        movies.add(
                            Movie(
                                title = detailJson.getString("Title"),
                                year = detailJson.getString("Year"),
                                rated = detailJson.getString("Rated"),
                                released = detailJson.getString("Released"),
                                runtime = detailJson.getString("Runtime"),
                                genre = detailJson.getString("Genre"),
                                director = detailJson.getString("Director"),
                                writer = detailJson.getString("Writer"),
                                actors = detailJson.getString("Actors"),
                                plot = detailJson.getString("Plot")
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Log any errors during the API call
            Log.e("MovieFetch", "Error searching for movies", e)
        }
        return@withContext movies
    }
}

