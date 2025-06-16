package com.example.dreamplanner

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.database.PlanViewModel
import com.example.dreamplanner.database.SleepEntry
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sleep(navController: NavController, viewModel: PlanViewModel) {
    val sleeps by viewModel.sleepEntries.observeAsState(emptyList())
    var showAll by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var showAddForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text("Your Sleep")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Recent Sleep Logs", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showAddForm = true }) {
                Text("Add Sleep Entry")
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (showAddForm) {
                AddSleepEntryForm(
                    onSave = { sleepEntry ->
                        viewModel.addSleepEntry(sleepEntry)
                        showAddForm = false
                    },
                    onCancel = { showAddForm = false }
                )
            }

            val displayedSleeps = if (showAll) sleeps else sleeps.takeLast(3)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                displayedSleeps.reversed().forEach { sleep ->
                    val zone = ZoneId.systemDefault()

                    val startInstant = Instant.ofEpochMilli(sleep.startTime)
                    val stopInstant = Instant.ofEpochMilli(sleep.stopTime)

                    val startLocalTime = startInstant.atZone(zone).toLocalTime()
                    val stopLocalTime = stopInstant.atZone(zone).toLocalTime()

                    val durationMillis = sleep.stopTime - sleep.startTime
                    val durationMinutes = (durationMillis / 1000 / 60).toInt()


                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val startStr = startLocalTime.format(timeFormatter)
                    val stopStr = stopLocalTime.format(timeFormatter)

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            Modifier
                                .padding(vertical = 4.dp)
                        ){
                            Text("Start: $startStr")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("End: $stopStr")
                            Spacer(modifier = Modifier.width(14.dp))
                            Button(onClick = {
                                viewModel.deleteSleepEntry(sleep)
                            }) {
                                Text("Delete")
                            }
                        }
                        Row(
                            Modifier
                                .padding(vertical = 4.dp)
                        ){
                            Text("Date: ${sleep.date}", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Duration: ${formatDuration(durationMinutes)}")
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (showAll) "Show less ▲" else "Show more ▼",
                    modifier = Modifier
                        .clickable { showAll = !showAll }
                        .align(Alignment.End),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Sleep Chart", fontSize = 20.sp)
            val sleepByDay = getSleepHoursByDay(sleeps, 7)

            Text("Sleep Hours Last 7 Days", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))

            SleepBarChart(
                data = sleepByDay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val last3DaysPercent = calculateSleepPercentage(sleeps, 3)
            val lastWeekPercent = calculateSleepPercentage(sleeps, 7)
            val last2WeeksPercent = calculateSleepPercentage(sleeps, 14)
            val lastMonthPercent = calculateSleepPercentage(sleeps, 30)

            val percentages = listOf(
                "Last 3 days" to last3DaysPercent,
                "Last week" to lastWeekPercent,
                "Last 2 weeks" to last2WeeksPercent,
                "Last month" to lastMonthPercent,
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(percentages) { (label, percent) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.primary),

                        elevation = cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(label, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$percent%", fontSize = 24.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return "${hours}h ${mins}min"
}

fun calculateSleepPercentage(
    sleeps: List<SleepEntry>,
    daysBack: Int
): Int {
    val now = System.currentTimeMillis()
    val periodStart = now - daysBack * 24 * 60 * 60 * 1000L

    // Weź tylko te wpisy, które zaczęły się po periodStart
    val filtered = sleeps.filter { it.startTime >= periodStart }

    // Suma snu w minutach
    val totalMinutes = filtered.sumOf { (it.stopTime - it.startTime) / 1000 / 60 }

    // Max snu to 8h na dobę * dni
    val maxMinutes = daysBack * 8 * 60

    // % wysypiania (int)
    return ((totalMinutes.toDouble() / maxMinutes) * 100).coerceIn(0.0, 100.0).toInt()
}

fun getSleepHoursByDay(sleeps: List<SleepEntry>, days: Int = 7): List<Pair<String, Float>> {
    val zone = ZoneId.systemDefault()
    val now = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MM-dd")

    return (0 until days).map { i ->
        val day = now.minusDays((days - 1 - i).toLong())
        val startOfDay = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val endOfDay = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        // Suma snu w tym dniu w godzinach (float)
        val totalMinutes = sleeps.filter {
            it.startTime < endOfDay && it.stopTime > startOfDay
        }.sumOf {
            val start = maxOf(it.startTime, startOfDay)
            val end = minOf(it.stopTime, endOfDay)
            (end - start).toDouble() / (1000 * 60)  // czas w minutach jako Double
        }

        day.format(formatter) to (totalMinutes / 60f).toFloat()
    }
}

@Composable
fun SleepBarChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    val maxHours = (data.maxOfOrNull { it.second } ?: 8f).coerceAtLeast(8f)
    val barWidth = 30.dp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (day, hours) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height((hours / maxHours * 150).dp)
                        .width(barWidth)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(day, fontSize = 12.sp)
                Text(String.format("%.1fh", hours), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AddSleepEntryForm(
    onSave: (SleepEntry) -> Unit,
    onCancel: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var startText by remember { mutableStateOf("") }
    var stopText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Add Sleep Entry", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = startText,
            onValueChange = { startText = it },
            label = { Text("Start time (HH:mm)") },
            singleLine = true,
            isError = errorMsg != null
        )
        OutlinedTextField(
            value = stopText,
            onValueChange = { stopText = it },
            label = { Text("Stop time (HH:mm)") },
            singleLine = true,
            isError = errorMsg != null
        )

        if (errorMsg != null) {
            Text(errorMsg!!, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(12.dp))

        Row {
            Button(onClick = {
                try {
                    val startTime = LocalTime.parse(startText, timeFormatter)
                    val stopTime = LocalTime.parse(stopText, timeFormatter)

                    val startDateTime: LocalDateTime
                    val stopDateTime: LocalDateTime

                    if (stopTime <= startTime) {
                        // spanienie przechodzi przez północ
                        startDateTime = LocalDateTime.of(today, startTime)
                        stopDateTime = LocalDateTime.of(today.plusDays(1), stopTime)
                    } else {
                        startDateTime = LocalDateTime.of(today, startTime)
                        stopDateTime = LocalDateTime.of(today, stopTime)
                    }

                    val zoneId = ZoneId.systemDefault()
                    val startMillis = startDateTime.atZone(zoneId).toInstant().toEpochMilli()
                    val stopMillis = stopDateTime.atZone(zoneId).toInstant().toEpochMilli()

                    // Tworzymy SleepEntry (uid nie podajemy, bo auto generowane)
                    val newEntry = SleepEntry(
                        startTime = startMillis,
                        stopTime = stopMillis,
                        date = today.toString()
                    )
                    onSave(newEntry)
                    errorMsg = null
                } catch (e: DateTimeParseException) {
                    errorMsg = "Invalid time format, use HH:mm"
                }
            }) {
                Text("Save")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

