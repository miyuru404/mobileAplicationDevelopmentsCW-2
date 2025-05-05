package com.example.futurework

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
             MainScreen()

        }
    }
}

@Composable
fun MainScreen() {
    val configuration = LocalConfiguration.current
    //val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
            .padding(16.dp)
    ) {
        if (isPortrait) {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.multimedia_logo_design___vijay_k_removebg_preview),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .absoluteOffset(x = 0.dp, y = 0.dp)
                )

                AddMoviesToTheDatabase()
                CustomButton("Search for Movies", SearchForMovies::class.java)
                CustomButton("Search for Actors", SearchForActor::class.java)

                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            // Landscape Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.multimedia_logo_design___vijay_k_removebg_preview),
                    contentDescription = "Logo",
                    modifier = Modifier.size(250.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AddMoviesToTheDatabase()
                    CustomButton("Search for Movies", SearchForMovies::class.java)
                    CustomButton("Search for Actors", SearchForActor::class.java)
                }
            }
        }
    }
}
@Composable
fun AddMoviesToTheDatabase() {
    val context = LocalContext.current
    Button(
        onClick = {

            val db = Room.databaseBuilder(context, MovieDatabase::class.java, "movies_db").build()
            val movieDao = db.movieDao()
            CoroutineScope(Dispatchers.IO).launch {
                movieDao.insertAll(getHardcodedMovies())
                // Retrieve and log the inserted movies
                val insertedMovies = movieDao.getAllMovies()
                insertedMovies.forEach {
                    Log.d("MovieDB", "Inserted: ${it.title}")
                }

                // Optional: show confirmation in a toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Movies added to DB!", Toast.LENGTH_SHORT).show()
                }


            }

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
fun CustomButton(buttonText: String, targetActivity: Class<*>) {
    val  context = LocalContext.current
    Button(
        onClick = { val intent = Intent(context, targetActivity)
                    context.startActivity(intent) },
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
            Text( buttonText)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }

    }
}

fun getHardcodedMovies(): List<Movie> {
    return listOf(
        Movie(
            title = "The Shawshank Redemption",
            year = "1994",
            rated = "R",
            released = "14 Oct 1994",
            runtime = "142 min",
            genre = "Drama",
            director = "Frank Darabont",
            writer = "Stephen King, Frank Darabont",
            actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
            plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
        ),
        Movie(
            title = "Batman: The Dark Knight Returns, Part 1",
            year = "2012",
            rated = "PG-13",
            released = "25 Sep 2012",
            runtime = "76 min",
            genre = "Animation, Action, Crime, Drama, Thriller",
            director = "Jay Oliva",
            writer = "Bob Kane (character created by: Batman), Frank Miller (comic book), Klaus Janson (comic book), Bob Goodman",
            actors = "Peter Weller, Ariel Winter, David Selby, Wade Williams",
            plot = "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl. But, does he still have what it takes to fight crime in a new era?"
        ),
        Movie(
            title = "The Lord of the Rings: The Return of the King",
            year = "2003",
            rated = "PG-13",
            released = "17 Dec 2003",
            runtime = "201 min",
            genre = "Action, Adventure, Drama",
            director = "Peter Jackson",
            writer = "J.R.R. Tolkien, Fran Walsh, Philippa Boyens",
            actors = "Elijah Wood, Viggo Mortensen, Ian McKellen",
            plot = "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring."
        ),
        Movie(
            title = "Inception",
            year = "2010",
            rated = "PG-13",
            released = "16 Jul 2010",
            runtime = "148 min",
            genre = "Action, Adventure, Sci-Fi",
            director = "Christopher Nolan",
            writer = "Christopher Nolan",
            actors = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
            plot = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster."
        ),
        Movie(
            title = "The Matrix",
            year = "1999",
            rated = "R",
            released = "31 Mar 1999",
            runtime = "136 min",
            genre = "Action, Sci-Fi",
            director = "Lana Wachowski, Lilly Wachowski",
            writer = "Lilly Wachowski, Lana Wachowski",
            actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
            plot = "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."
        )
    )
}




