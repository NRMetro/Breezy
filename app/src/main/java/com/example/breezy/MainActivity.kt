package com.example.breezy

import android.content.Context
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breezy.ui.theme.BreezyTheme
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreezyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FrontScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FrontScreen(modifier: Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
    ){
        Row(
            Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(6.dp)
        ){
            Text(
                text = context.getString(R.string.app_name)
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = context.getString(R.string.location)
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
                    val temp = context.getString(R.string.temp)
                    Text(
                        text = "$temp°",
                        fontSize = 60.sp
                    )
                    val feelsLike = context.getString(R.string.feels_like)
                    Text(
                        text = "Feels like $feelsLike°",
                        modifier = Modifier.padding(start = 6.dp,top = 4.dp)
                    )
                }
                val low = context.getString(R.string.low)
                val high = context.getString(R.string.high)
                val humidity = context.getString(R.string.humidity)
                val pressure = context.getString(R.string.pressure)
                Column{
                    Text("Low $low°")
                    Text("High $high°")
                    Text("Humidity $humidity%")
                    Text("Pressure $pressure hPa")
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
        FrontScreen(modifier = Modifier)
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