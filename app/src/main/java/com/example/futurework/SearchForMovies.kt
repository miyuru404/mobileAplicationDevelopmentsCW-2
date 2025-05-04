package com.example.futurework

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
import androidx.compose.ui.unit.dp

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ){
        Column {
            TextBox()
            Spacer(modifier = Modifier.height(10.dp))
            RetrieveMovie()
            Spacer(modifier = Modifier.height(10.dp))
            SaveMovie()
        }
    }

}

@Composable
fun TextBox() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Enter your name") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun SaveMovie() {

    Button(
        onClick = {  },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB2BDE2),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()              // makes the button take full horizontal space
            .height(60.dp),              // makes the button taller
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 20.dp,     // creates the shadow
            pressedElevation = 12.dp     // deeper shadow when pressed
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
@Composable
fun RetrieveMovie() {
    var movie by remember { mutableStateOf<Movie?>(null) }


        Button(
            onClick = {
                // Simulated JSON string (not real parsing)
                val json = """
                {
                    "title": "Inception",
                    "director": "Christopher Nolan",
                    "year": "2010",
                    "genre": "Sci-Fi"
                }
            """.trimIndent()

                // Manually extract values from the string
                val title = getValue(json, "title")
                val director = getValue(json, "director")
                val year = getValue(json, "year")
                val genre = getValue(json, "genre")

                movie = Movie(title, director, year, genre)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB2BDE2),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()              // makes the button take full horizontal space
                .height(60.dp),              // makes the button taller
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 20.dp,     // creates the shadow
                pressedElevation = 12.dp     // deeper shadow when pressed
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

        movie?.let {
            Text("Title: ${it.title}")
            Text("Director: ${it.director}")
            Text("Year: ${it.year}")
            Text("Genre: ${it.genre}")
        }
}


data class Movie(
    val title: String,
    val director: String,
    val year: String,
    val genre: String
)
fun getValue(json: String, key: String): String {
    val regex = """"$key"\s*:\s*"([^"]*)"""".toRegex()
    return regex.find(json)?.groupValues?.get(1) ?: "N/A"
}

