package com.example.breezy

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breezy.ui.theme.BreezyTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreezyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FrontScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ){
        Row(
            Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(6.dp)
        ){
            Text(
                text = "Breezy"
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Excelsior, MN"
            )
        }

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(.5f)
            ){
                Column (
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 18.dp)

                ){
                    Text(
                        text = "72°",
                        fontSize = 60.sp
                    )
                    Text(
                        text = "Feels like 78°",
                        modifier = Modifier.padding(start = 6.dp,top = 4.dp)
                    )
                }
                Column{
                    Text("Low 65°")
                    Text("High 80°")
                    Text("Humidity 98%")
                    Text("Pressure 1234 hPa")
                }
            }
            Column{
                val image = painterResource(R.drawable.sun)
                Image(
                    painter = image,
                    contentDescription = "Sunny",
                    modifier = Modifier
                        .fillMaxWidth(.36f)
                        .padding(top = 14.dp)
                )

            }
        }
        Row{

        }
    }
}

@Preview(showBackground = true)
@Composable
fun FrontPreview(){
    BreezyTheme {
        FrontScreen()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BreezyTheme {
        Greeting("Android")
    }
}