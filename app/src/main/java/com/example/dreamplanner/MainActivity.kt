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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import java.nio.file.WatchEvent


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Dream Planner", fontSize = 22.sp)
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Text(text = "👤", fontSize = 20.sp)
                        // Lub zamiast emoji możesz użyć:
                        // Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)
        ) {
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
                composable("profile") {
                    ProfileScreen(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}



@Composable
fun BottomBar(navController: NavController) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary) // tło całego paska
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            "screen1" to R.drawable.house,
            "screen2" to R.drawable.moon,
            "screen3" to R.drawable.notepad,
            "screen4" to R.drawable.book
        )

        navItems.forEach { (route, imageResource) ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .padding(horizontal = 4.dp)
                    .clickable {
                        navController.navigate(route)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .aspectRatio(1f)
                            .background(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary, // title color
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Example user info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Username: Dreamer",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: dreamer@example.com",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Back to Main")
        }
    }
}




class MainViewModel : ViewModel(){
    var plans = mutableStateListOf<Plan>().apply { addAll(samplePlans) }
    var goals = mutableStateListOf<Goal>().apply { addAll(sampleGoals) }
    var dailyTasks = mutableStateListOf<DailyTask>().apply { addAll(sampleDailyTasks) }
    var sleepEntries = mutableStateListOf<SleepEntry>().apply { addAll(sampleSleepEntries) }

}