package com.example.dreamplanner

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamplanner.database.*
import com.example.dreamplanner.ui.theme.DreamPlannerTheme
import kotlinx.coroutines.launch
import java.util.Calendar
import android.Manifest
private const val CALENDAR_PERMISSION_CODE = 101

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ukryj pasek statusu i nawigacji
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
        }

        // Żądanie uprawnień
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALENDAR),
                CALENDAR_PERMISSION_CODE
            )
        } else {
            syncCalendarPlans()
        }

        setContent {
            DreamPlannerTheme {
                Main()
            }
        }
    }

    // Obsługa odpowiedzi użytkownika na żądanie uprawnienia
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALENDAR_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            syncCalendarPlans()
        }
    }

    // Rozszerzenie klasy MainActivity – masz dostęp do lifecycleScope i this
    private fun MainActivity.syncCalendarPlans() {
        val viewModel = ViewModelProvider(this)[PlanViewModel::class.java]

        lifecycleScope.launch {
            val calendarPlans = getTodayCalendarEvents(this@syncCalendarPlans)

            viewModel.deleteAllPlans()
            viewModel.clearAndInsertPlans(calendarPlans + samplePlans)
        }
    }
}

fun getTodayCalendarEvents(context: Context): List<Plan> {
    val contentResolver = context.contentResolver
    val events = mutableListOf<Plan>()

    val startOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val projection = arrayOf("title", "dtstart", "eventLocation")
    val selection = "(dtstart >= ?) AND (dtstart <= ?)"
    val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())

    val cursor = contentResolver.query(
        Uri.parse("content://com.android.calendar/events"),
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        val titleIndex = it.getColumnIndex("title")
        val dateIndex = it.getColumnIndex("dtstart")
        val placeIndex = it.getColumnIndex("eventLocation")

        while (it.moveToNext()) {
            val title = it.getString(titleIndex) ?: "Bez tytułu"
            val date = it.getLong(dateIndex)
            val place = it.getString(placeIndex) ?: "Brak lokalizacji"
            events.add(Plan(name = title, date = date, priority = 1, place = place))
        }
    }

    return events
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val viewModel: PlanViewModel = viewModel()
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) } // <-- stan logowania

    Scaffold(
        topBar = {
            if (isLoggedIn) {
                TopAppBar(
                    title = { Text("Dream Planner", fontSize = 22.sp) },
                    actions = {
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isLoggedIn) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = if (isLoggedIn) "screen1" else "login") {
                composable("login") {
                    LoginScreen(navController) {
                        isLoggedIn = true
                    }
                }
                composable("screen1") {
                    Screen1(navController = navController, viewModel)
                }
                composable("screen2") {
                    Screen2(navController = navController, viewModel)
                }
                composable("screen3") {
                    Screen3(navController = navController, viewModel)
                }
                composable("screen4") {
                    Screen4(navController = navController, viewModel)
                }
                composable("profile") {
                    ProfileScreen(navController = navController, viewModel)
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
fun ProfileScreen(navController: NavController, viewModel: PlanViewModel) {
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