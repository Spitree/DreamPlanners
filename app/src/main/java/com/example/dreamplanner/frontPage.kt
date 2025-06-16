package com.example.dreamplanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.database.PlanViewModel
import com.example.dreamplanner.ui.theme.accent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
import androidx.compose.material3.TopAppBarDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrontPage(navController: NavController, viewModel: PlanViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Obserwujemy dane snu z LiveData
    val sleepEntries by viewModel.sleepEntries.observeAsState(emptyList())
    val plans by viewModel.plans.observeAsState(emptyList())
    val goals by viewModel.goals.observeAsState(emptyList())
    val dailyTasks by viewModel.dailyTasks.observeAsState(emptyList())

    // Sortowanie planów malejąco po priorytecie
    val sortedPlans = plans.sortedByDescending { it.priority }

    fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}h ${minutes}min"
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text("Sleep Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Text("Last 3 days:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(Modifier.height(8.dp))

                val sleepByDay = getSleepHoursByDay(sleepEntries, days = 3)

                if (sleepByDay.isEmpty()) {
                    Text("No sleep data", color = Color.Black)
                } else {
                    sleepByDay.forEach { (date, hours) ->
                        val totalMinutes = (hours * 60).toInt()
                        Text("$date: ${formatDuration(totalMinutes)}", color = Color.Black)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Average sleep:", fontWeight = FontWeight.SemiBold, color = Color.Black)

                    val avgMinutes = sleepByDay.map { it.second * 60 }.average().toInt()

                    Text("Średnia długość snu: ${formatDuration(avgMinutes)}", color = Color.Blue)
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Overview")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    var expandedPlans by remember { mutableStateOf(false) }
                    var expandedGoals by remember { mutableStateOf(false) }
                    var expandedTasks by remember { mutableStateOf(false) }

                    Text("Dzisiejsze plany", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    val plansToShow = if (expandedPlans) sortedPlans else sortedPlans.take(3)

                    AnimatedVisibility(visible = true) {
                        Column {
                            plansToShow.forEach { plan ->
                                var checkedPlan by remember { mutableStateOf(false) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .background(accent)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(plan.name, modifier = Modifier.weight(1f))
                                    Text("Importance: ${plan.priority}", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Checkbox(
                                        checked = checkedPlan,
                                        onCheckedChange = { checkedPlan = it })
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { expandedPlans = !expandedPlans },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (expandedPlans) "Pokaż mniej" else "Pokaż więcej")
                    }


                    Spacer(Modifier.height(16.dp))

                    Text("Twoje cele", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    val goalsToShow = if (expandedGoals) goals else goals.take(3)

                    AnimatedVisibility(visible = true) {
                        Column {
                            goalsToShow.forEach { goal ->
                                var checkedGoal by remember { mutableStateOf(false) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .background(accent)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(goal.name, modifier = Modifier.weight(1f))
                                    Checkbox(
                                        checked = checkedGoal,
                                        onCheckedChange = { checkedGoal = it })
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { expandedGoals = !expandedGoals },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (expandedGoals) "Pokaż mniej" else "Pokaż więcej")
                    }


                    Spacer(Modifier.height(16.dp))

                    Text("Dzienne zajęcia", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    val tasksToShow = if (expandedTasks) dailyTasks else dailyTasks.take(3)

                    AnimatedVisibility(visible = true) {
                        Column {
                            tasksToShow.forEach { task ->
                                var checkedTask by remember { mutableStateOf(false) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .background(accent)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(task.name, modifier = Modifier.weight(1f))
                                    Checkbox(
                                        checked = checkedTask,
                                        onCheckedChange = { checkedTask = it })
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { expandedTasks = !expandedTasks },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (expandedTasks) "Pokaż mniej" else "Pokaż więcej")
                    }
                }
            }
        }
    )
}
