package com.example.futurework

import android.content.res.Configuration
import android.os.Bundle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.futurework.ui.theme.FutureWorkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchForActor : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            ThirdScreen()
        }
    }
}

@Composable
fun ThirdScreen() {
    var actorName by rememberSaveable { mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }

    val context = LocalContext.current
    val db = MovieDatabase.getInstance(context)
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ) {
        Column {
            TextBox2(actorName) { actorName = it }
            Spacer(modifier = Modifier.height(10.dp))

            if (isPortrait) {
                // Portrait layout
                Search(actorName, db) { results -> searchResults = results }
                Spacer(modifier = Modifier.height(16.dp))
                DisplayAllMovies(searchResults)
            } else {
                // Landscape layout
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Search(actorName, db) { results -> searchResults = results }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(2f)) {
                        DisplayAllMovies(searchResults)
                    }
                }
            }
        }
    }
}


@Composable
fun TextBox2(actorName: String, onTextChange: (String) -> Unit) {
    // OutlinedTextField will update the actorName when the user types in it
    OutlinedTextField(
        value = actorName,
        onValueChange = onTextChange,
        label = { Text("Search for actors") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun Search(actorName: String, db: MovieDatabase, onResults: (List<Movie>) -> Unit) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val result  = db.movieDao().searchMoviesByActor(actorName.trim())
                withContext(Dispatchers.Main) {
                    onResults(result)
                    println(result)
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
            Text("Search")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    }
}

@Composable
fun DisplayAllMovies(movies: List<Movie>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Loop through each movie in the list and call DisplayMovieData
        movies.forEach { movie ->
            // Call DisplayMovieData to show details for each movie
            DisplayMovieData(movies) // Pass a list containing the single movie
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}




