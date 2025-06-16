package com.example.dreamplanner

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

    // Ostatnie 3 dni snu (posortowane od najnowszego)
    val recentSleep = sleepEntries.sortedByDescending { it.uid }.take(3)

    fun formatTime(hhmm: Int): String {
        val hours = hhmm / 100
        val minutes = hhmm % 100
        return String.format("%02d:%02d", hours, minutes)
    }
    fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}h ${minutes}min"
    }

    data class SleepDuration(val startStr: String, val endStr: String, val durationMinutes: Int)

    val sleepDurations = recentSleep.map { entry ->
        val start = entry.start.toInt()
        val end = entry.end.toInt()

        val (startH, startM) = Pair(start / 100, start % 100)
        val (endH, endM) = Pair(end / 100, end % 100)

        val startInMinutes = startH * 60 + startM
        val endInMinutes = endH * 60 + endM

        val duration = if (endInMinutes >= startInMinutes) {
            endInMinutes - startInMinutes
        } else {
            (24 * 60 - startInMinutes) + endInMinutes
        }

        SleepDuration(
            startStr = formatTime(start),
            endStr = formatTime(end),
            durationMinutes = duration
        )
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .background(color= MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text("Sleep Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Text("Last 3 days:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(Modifier.height(8.dp))

                if (sleepDurations.isEmpty()) {
                    Text("No sleep data", color = Color.Black)
                } else {
                    sleepDurations.forEach {
                        Text("${it.startStr} - ${it.endStr}: ${formatDuration(it.durationMinutes)}", color = Color.Black)
                    }
                Spacer(Modifier.height(16.dp))

                Text("Average sleep:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    val lastThree = sleepDurations.takeLast(3)
                    val averageMinutes = lastThree.sumOf { it.durationMinutes } / lastThree.size
                    Text("Średnia długość snu: ${formatDuration(averageMinutes)}", color = Color.Blue)
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
                    Text("Dzisiejsze plany", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    val plansToShow = if (expanded) sortedPlans else sortedPlans.take(3)

                    plansToShow.forEach { plan ->
                        var checked by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .background(accent)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(plan.name, modifier = Modifier.weight(1f))
                            Text(" (prio: ${plan.priority})", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (expanded) "Pokaż mniej" else "Pokaż więcej")
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Twoje cele", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                    goals.forEach { goal ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .background(accent)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(goal.name, modifier = Modifier.weight(1f))
                            Checkbox(checked = goal.completed, onCheckedChange = { /* TODO: obsługa */ })
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Dzienne zajęcia", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                    dailyTasks.forEach { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .background(accent)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(task.name, modifier = Modifier.weight(1f))
                            Checkbox(checked = task.completed, onCheckedChange = { /* TODO: obsługa */ })
                        }
                    }
                }
            }
        }
    )
}
