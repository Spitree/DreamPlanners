package com.example.dreamplanner

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.database.*
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShareButton(context: Context) {
    Button(onClick = {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Dream Planner")
            putExtra(Intent.EXTRA_TEXT, "Oto mój dzisiejszy plan z aplikacji Dream Planner!")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Udostępnij przez:"))
    }) {
        Text("Udostępnij")
    }
}

@Composable
fun SmsButton() {
    val context = LocalContext.current

    Button(onClick = {
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("smsto:") // lub wpisz numer np. "smsto:123456789"
            putExtra("sms_body", "Oto mój plan dnia z Dream Planner!")
        }
        context.startActivity(smsIntent)
    }) {
        Text("Wyślij plan przez SMS")
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun Plans(navController: NavController, viewModel: PlanViewModel) {
    val tabTitles = listOf("Plans", "Goals", "Daily")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Observe LiveData
    val plans by viewModel.plans.observeAsState(emptyList())
    val dailies by viewModel.dailyTasks.observeAsState(emptyList())
    val goals by viewModel.goals.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Your Planner")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> PlansTab(plans,viewModel)
                    1 -> GoalsTab(goals,viewModel)
                    2 -> DailyTab(dailies,viewModel)
                }
            }
        }
    }
}

@Composable
fun PlansTab(plans: List<Plan>, viewModel: PlanViewModel) {
    val scope = rememberCoroutineScope()
    var showForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(plans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = plan.name, fontSize = 20.sp)
                            Text(text = "Priorytet: ${plan.priority}")
                            Text(text = "Miejsce: ${plan.place}")
                            Text(text = "Data: ${SimpleDateFormat("yyyy-MM-dd").format(Date(plan.date))}")
                        }
                        Checkbox(
                            checked = plan.completed,
                            onCheckedChange = { checked ->
                                viewModel.togglePlanCompleted(plan, checked)
                            }
                        )
                        IconButton(onClick = {
                            viewModel.deletePlan(plan)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń plan")
                        }
                    }
                }
            }
        }

        if (showForm) {
            AddPlanForm(
                onAdd = { plan ->
                    viewModel.addPlan(plan)
                    showForm = false
                },
                onCancel = {
                    showForm = false
                }
            )
        } else {
            Button(
                onClick = { showForm = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj nowy plan")
            }
        }
    }
}


@Composable
fun GoalsTab(goals: List<Goal>, viewModel: PlanViewModel) {
    val checkedStates = remember { mutableStateMapOf<Int, Boolean>() }
    var showForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(goals) { goal ->
                val isChecked = checkedStates[goal.uid] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = goal.name, fontSize = 18.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedStates[goal.uid] = it }
                        )
                        IconButton(onClick = {
                            viewModel.deleteGoal(goal)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń plan")
                        }
                    }
                }
            }
        }

        if (showForm) {
            AddGoalForm (
                onAdd = { goal ->
                    viewModel.addGoal(goal)
                    showForm = false
                },
                onCancel = {
                    showForm = false
                }
            )
        } else {
            Button(
                onClick = { showForm = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj nowy plan")
            }
        }
    }
}

@Composable
fun DailyTab(tasks: List<DailyTask>, viewModel: PlanViewModel) {
    val context = LocalContext.current
    val checkedStates = remember { mutableStateMapOf<Int, Boolean>() }
    var showForm by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(tasks) { task ->
                val isChecked = checkedStates[task.uid] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = task.name, fontSize = 18.sp)
                        Text(text = "Priorytet: ${task.priority}")
                        Text(text = "Opis: ${task.description}")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Wykonane")
                            Spacer(Modifier.width(8.dp))
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedStates[task.uid] = it }
                            )
                            IconButton(onClick = {
                                viewModel.deleteDailyTask(task)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń")
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ShareButton(context)
                            Spacer(modifier = Modifier.height(8.dp))
                            SmsButton()
                        }
                    }
                }
            }
        }

        if (showForm) {
            AddDailyTaskForm (
                onAdd = { task ->
                    viewModel.addDailyTask(task)
                    showForm = false
                },
                onCancel = {
                    showForm = false
                }
            )
        } else {
            Button(
                onClick = { showForm = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj nowy")
            }
        }
    }
}


@Composable
fun AddPlanForm(
    onAdd: (Plan) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priorityText by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    val dateFormat = remember { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var dateError by remember { mutableStateOf(false) }
    var priorityError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = priorityText,
            onValueChange = { priorityText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Priorytet (liczba)") },
            isError = priorityError,
            modifier = Modifier.fillMaxWidth()
        )
        if (priorityError) {
            Text("Wprowadź poprawny priorytet", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("Miejsce") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Data (yyyy-MM-dd)") },
            isError = dateError,
            modifier = Modifier.fillMaxWidth()
        )
        if (dateError) {
            Text("Wprowadź poprawną datę", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))

        Row {
            Button(
                onClick = {
                    dateError = false
                    priorityError = false
                    val dateLong = try {
                        dateFormat.parse(dateText)?.time ?: throw Exception()
                    } catch (e: Exception) {
                        dateError = true
                        null
                    }
                    val priority = priorityText.toIntOrNull()
                    if (priority == null) {
                        priorityError = true
                    }

                    if (!dateError && !priorityError && name.isNotBlank() && place.isNotBlank() && dateLong != null) {
                        val newPlan = Plan(
                            name = name.trim(),
                            priority = priority!!,
                            place = place.trim(),
                            date = dateLong,
                            completed = false
                        )
                        onAdd(newPlan)

                        // Reset formularza
                        name = ""
                        priorityText = ""
                        place = ""
                        dateText = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Dodaj")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj")
            }
        }
    }
}

@Composable
fun AddDailyTaskForm(
    onAdd: (DailyTask) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priorityText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") } // np. "2025-06-16"

    val dateFormat = remember { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var dateError by remember { mutableStateOf(false) }
    var priorityError by remember { mutableStateOf(false) }


    // Funkcja pomocnicza do parsowania daty z String do timestamp (ms)
    fun parseDate(dateStr: String): Long? {
        return try {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
            formatter.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa zadania") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = priorityText,
            onValueChange = { priorityText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Priorytet (liczba)") },
            isError = priorityError,
            modifier = Modifier.fillMaxWidth()
        )
        if (priorityError) {
            Text("Wprowadź poprawny priorytet", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Data (yyyy-MM-dd)") },
            isError = dateError,
            modifier = Modifier.fillMaxWidth()
        )
        if (dateError) {
            Text("Wprowadź poprawną datę", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = {
                    val priority = priorityText.toIntOrNull() ?: 0
                    val date = parseDate(dateText) ?: System.currentTimeMillis()

                    if (name.isNotBlank()) {
                        onAdd(
                            DailyTask(
                                name = name.trim(),
                                priority = priority,
                                description = description.trim(),
                                date = date,
                                completed = false
                            )
                        )
                        // Reset pól
                        name = ""
                        priorityText = ""
                        description = ""
                        dateText = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Dodaj")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj")
            }
        }
    }
}

@Composable
fun AddGoalForm(
    onAdd: (Goal) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa celu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(Goal(name = name.trim(), completed = false))
                        name = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Dodaj")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj")
            }
        }
    }
}

