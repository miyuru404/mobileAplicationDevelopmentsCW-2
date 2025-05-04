package com.example.futurework

import android.content.Intent
import android.os.Bundle
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

                CustomButton("Add Movies to DB", SearchForMovies::class.java)
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
                    CustomButton("Add Movies to DB", SearchForMovies::class.java)
                    CustomButton("Search for Movies", SearchForMovies::class.java)
                    CustomButton("Search for Actors", SearchForActor::class.java)
                }
            }
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



