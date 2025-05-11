package com.example.dreamplanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel
import com.example.dreamplanner.*
import com.example.dreamplanner.Models.*
import com.example.dreamplanner.ui.theme.DreamPlannerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DreamPlannerTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()

    Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)) {
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
fun BottomBar(navController: NavController) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        listOf(
            "screen1" to R.drawable.house,
            "screen2" to R.drawable.moon,
            "screen3" to R.drawable.notepad,
            "screen4" to R.drawable.book
        ).forEach { (route, imageResource) ->
            Box(
                modifier = Modifier
                    .height(40.dp) // kontroluje wysokość obrazka
                    .weight(1f)   // każdy element zajmuje tyle samo miejsca
                    .clickable {
                        navController.navigate(route)
                    }
                    .padding(horizontal = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = null, // brak opisu, bo label usunięty
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

class MainViewModel : ViewModel(){
    var plans = mutableStateListOf<Plan>().apply { addAll(samplePlans) }
    var goals = mutableStateListOf<Goal>().apply { addAll(sampleGoals) }
    var dailyTasks = mutableStateListOf<DailyTask>().apply { addAll(sampleDailyTasks) }
    var sleepEntries = mutableStateListOf<SleepEntry>().apply { addAll(sampleSleepEntries) }

}