package com.example.futurework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.futurework.ui.theme.FutureWorkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
             mainScreen()

        }
    }
}

@Composable
fun mainScreen() {

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color(0xFFFFC677))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            CustomButton("Add Movies to DB")
            CustomButton("Search for Movies")
            CustomButton("Search for Actors")
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}



@Composable
fun CustomButton(buttonText: String) {
    Button(
        onClick = { /* Handle click */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA2A2A2),
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()              // makes the button take full horizontal space
            .height(60.dp),              // makes the button taller
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 20.dp,     // creates the shadow
            pressedElevation = 12.dp     // deeper shadow when pressed
        )
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyLarge, // larger text style
            modifier = Modifier.padding(6.dp)
        )
    }
}



