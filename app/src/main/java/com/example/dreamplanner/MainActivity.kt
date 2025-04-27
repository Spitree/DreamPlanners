package com.example.dreamplanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        // Wspólny nagłówek
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Dream Planner",
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        // Nawigacja między ekranami
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = "screen1") {
                composable("screen1") {
                    Screen1(navController = navController, viewModel, plans = samplePlans)
                }
                composable("screen2") {
                    Screen2(navController = navController, viewModel, sleeps = samplePlans)
                }
                composable("screen3") {
                    Screen3(navController = navController, viewModel, articles = samplePlans)
                }
                composable("screen4") {
                    Screen4(navController = navController, viewModel, plans = samplePlans)
                }
            }
        }

        BottomBar(navController = navController)
    }
}

@Composable
fun Screen1(navController: NavController,viewModel: MainViewModel,
            plans: List<Plan>
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Main Screen", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(plans) { plan ->
                Text(
                    text = plan.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Main Screen")
        }
    }
}

@Composable
fun Screen2(navController: NavController,viewModel: MainViewModel, sleeps: List<Plan>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Sleep", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(sleeps) { sleeps ->
                Text(
                    text = sleeps.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Main Screen")
        }
    }
}

@Composable
fun Screen3(navController: NavController,viewModel: MainViewModel, articles: List<Plan>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dream Archives", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(articles) { articles ->
                Text(
                    text = articles.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Main Screen")
        }
    }
}

@Composable
fun Screen4(navController: NavController,viewModel: MainViewModel, plans: List<Plan>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Plans", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(plans) { plans ->
                Text(
                    text = plans.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Main Screen")
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            "1" to "screen1",
            "2" to "screen2",
            "3" to "screen3",
            "4" to "screen4"
        ).forEach { (label, route) ->
            Button(
                onClick = { navController.navigate(route) },
                modifier = Modifier
                    .height(40.dp) // niższe przyciski
                    .weight(1f)   // każdy przycisk zajmie tyle samo miejsca
                    .padding(horizontal = 4.dp) // trochę odstępu między przyciskami
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp // mniejsza czcionka
                )
            }
        }
    }
}

data class Plan(
    val name: String,
    val date: Int,
    val prio: Int,
    val place: String
)

val samplePlans = listOf(
    Plan(name = "Nauka Kotlin", date = 20250430, prio = 1, place = "Dom"),
    Plan(name = "Zakupy spożywcze", date = 20250428, prio = 2, place = "Supermarket"),
    Plan(name = "Spotkanie z przyjacielem", date = 20250502, prio = 1, place = "Kawiarnia"),
    Plan(name = "Trening na siłowni", date = 20250429, prio = 3, place = "Siłownia"),
    Plan(name = "Wizyta u lekarza", date = 20250501, prio = 1, place = "Przychodnia"),
    Plan(name = "Wycieczka rowerowa", date = 20250504, prio = 2, place = "Park"),
    Plan(name = "Prezentacja projektu", date = 20250505, prio = 1, place = "Biuro"),
    Plan(name = "Seans filmowy", date = 20250430, prio = 3, place = "Kino"),
    Plan(name = "Porządki w domu", date = 20250427, prio = 2, place = "Dom"),
    Plan(name = "Weekendowy wypad", date = 20250506, prio = 2, place = "Góry")
)


class MainViewModel : ViewModel(){

}