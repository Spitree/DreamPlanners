package com.example.dreamplanner

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CheckboxDefaults.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun ShareButton(context: Context, task: DailyTask) {
    val shareBody = remember(task) {
        if (task == null) {
            "Mój plan dnia jest pusty."
        } else {
            buildString {
                append("Mój plan dnia z Dream Planner:\n")
                append("${task.name}\n")
                append("Priorytet: ${task.priority}\n")
                append("Opis: ${task.description}\n\n")
            }
        }
    }

    Button(
        onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Dream Planner")
                putExtra(Intent.EXTRA_TEXT, shareBody)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Udostępnij przez:"))
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.surface,
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Text("Udostępnij", color = Color.Black)
    }
}

@Composable
fun SmsButton(task: DailyTask) {
    val context = LocalContext.current

    // Tworzymy tekst wiadomości z listy tasków
    val smsBody = remember(task) {
        if (task == null) {
            "Mój plan dnia jest pusty."
        } else {
            buildString {
                append("Mój plan dnia z Dream Planner:\n")
                append("${task.name}\n")
                append("Priorytet: ${task.priority}\n")
                append("Opis: ${task.description}\n\n")
            }
        }
    }

    Button(
        onClick = {
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", smsBody)
            }
            context.startActivity(smsIntent)
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Text("Wyślij plan przez SMS", color = Color.Black)
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
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Your Planner",color = Color.Black)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            val selectedTabIndex = pagerState.currentPage
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                title,
                                color = if (isSelected) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.secondary,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) { page ->
                when (page) {
                    0 -> PlansTab(plans, viewModel)
                    1 -> GoalsTab(goals, viewModel)
                    2 -> DailyTab(dailies, viewModel)
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
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = plan.name, fontSize = 20.sp,color = Color.Black)
                            Text(text = "Priorytet: ${plan.priority}",color = Color.Black)
                            Text(text = "Miejsce: ${plan.place}",color = Color.Black)
                            Text(text = "Data: ${SimpleDateFormat("yyyy-MM-dd").format(Date(plan.date))}",color = Color.Black)
                        }
                        Checkbox(
                            checked = plan.completed,
                            onCheckedChange = { checked ->
                                viewModel.togglePlanCompleted(plan, checked)
                            }
                        )
                        IconButton(
                            onClick = { viewModel.deletePlan(plan) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.kosz),
                                contentDescription = "Usuń plan",
                                modifier = Modifier.size(48.dp) // Dopasuj rozmiar ikony
                            )
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Text("Dodaj nowy plan",color = Color.Black)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),

                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = goal.name, fontSize = 18.sp,color = Color.Black)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = goal.completed,
                            onCheckedChange = { checked ->
                                viewModel.toggleGoalCompleted(goal, checked)
                            }
                        )
                        IconButton(
                            onClick = { viewModel.deleteGoal(goal) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.kosz),
                                contentDescription = "Usuń plan",
                                modifier = Modifier.size(48.dp) // Dopasuj rozmiar ikony
                            )
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Text("Dodaj nowy plan",color = Color.Black)
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = task.name, fontSize = 18.sp,color = Color.Black)
                        Text(text = "Priorytet: ${task.priority}",color = Color.Black)
                        Text(text = "Opis: ${task.description}",color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Wykonane",color = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Checkbox(
                                checked = task.completed,
                                onCheckedChange = { checked ->
                                    viewModel.toggleDailyTaskCompleted(task, checked)
                                }
                            )
                            IconButton(
                                onClick = { viewModel.deleteDailyTask(task) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Color.Red
                                )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.kosz),
                                    contentDescription = "Usuń plan",
                                    modifier = Modifier.size(48.dp) // Dopasuj rozmiar ikony
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            ShareButton(context,task = task)
                            Spacer(modifier = Modifier.width(8.dp))
                            SmsButton(task = task)
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Text("Dodaj nowy plan",color = Color.Black)
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
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = priorityText,
            onValueChange = { priorityText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Priorytet (liczba)") },
            isError = priorityError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        if (priorityError) {
            Text("Wprowadź poprawny priorytet", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("Miejsce") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Data (yyyy-MM-dd)") },
            isError = dateError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
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
                Text("Dodaj",color = Color.Black)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj",color = Color.Black)
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
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = priorityText,
            onValueChange = { priorityText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Priorytet (liczba)") },
            isError = priorityError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        if (priorityError) {
            Text("Wprowadź poprawny priorytet", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Data (yyyy-MM-dd)") },
            isError = dateError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
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
                Text("Dodaj",color = Color.Black)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj",color = Color.Black)
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
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            )
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
                Text("Dodaj",color = Color.Black)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj",color = Color.Black)
            }
        }
    }
}

