package com.example.dreamplanner

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamplanner.database.*
import com.example.dreamplanner.ui.theme.DreamPlannerTheme
import kotlinx.coroutines.launch
import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

private const val CALENDAR_PERMISSION_CODE = 101

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window,window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
        }
        val database = AppDatabase.getInstance(application)
        val repository = PlanRepository(
            database.planDao(),
            database.goalDao(),
            database.dailyTaskDao()
        )

        val viewModelFactory = PlanViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[PlanViewModel::class.java]

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
                Main(viewModel)
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

    private fun MainActivity.syncCalendarPlans() {
        val viewModel = ViewModelProvider(this)[PlanViewModel::class.java]

        lifecycleScope.launch {
            val calendarPlans = getTodayCalendarEvents(this@syncCalendarPlans)

            viewModel.deleteAllPlans()
            viewModel.clearAndInsertPlans(calendarPlans + samplePlans)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(viewModel: PlanViewModel) {
    val viewModel: PlanViewModel = viewModel
    val navController = rememberNavController()
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dream Planner", fontSize = 22.sp, color = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        if (viewModel.isLoggedIn) {
                            Text("Witaj ${viewModel.loggedInUsername}", fontSize = 16.sp, color = Color.Black) // lub np. ikona "zalogowany"
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Usuń plan",
                            modifier = Modifier.size(24.dp) // Dopasuj rozmiar ikony
                        )
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
            NavHost(navController = navController, startDestination = "splash") {
                composable ("splash") {
                    SplashScreen(navController = navController);
                }
                composable("frontPage") {
                    FrontPage(navController = navController, viewModel)
                }
                composable("login") {
                    LoginScreen(navController,viewModel) {isLoggedIn = true}
                }
                composable("sleep") {
                    Sleep(navController = navController, viewModel)
                }
                composable("articles") {
                    Articles(navController = navController, viewModel,isLoggedIn)
                }
                composable("plans") {
                    Plans(navController = navController, viewModel)
                }
                composable("profile") {
                    ProfileScreen(navController = navController, viewModel)
                }
                composable("register") {
                    RegisterScreen(navController, viewModel)
                }
            }
        }
    }
}



@Composable
fun BottomBar(navController: NavController) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary) // tło całego paska
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            "frontPage" to R.drawable.house,
            "sleep" to R.drawable.moon,
            "plans" to R.drawable.logo,
            "articles" to R.drawable.book
        )

        navItems.forEach { (route, imageResource) ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onSecondary)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: PlanViewModel) {
    val isLoggedIn = viewModel.isLoggedIn
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", fontSize = 28.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (viewModel.isLoggedIn && viewModel.loggedInUsername != null)
                        "Username: ${viewModel.loggedInUsername}"
                    else
                        "You are not logged in",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (viewModel.isLoggedIn && viewModel.loggedInUsername != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: ${viewModel.loggedInUsername}@example.com",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoggedIn) {
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Login", color = Color.Black)
            }
        } else {
            Button(
                onClick = {
                    viewModel.logout()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Logout", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Back to Main Menu", color = Color.Black)
        }
    }
}

@Composable
fun SplashAnimation() {
    AndroidView(
        modifier = Modifier.size(150.dp),
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageResource(R.drawable.logo)

                val animator = ObjectAnimator.ofFloat(this, "rotation", -25f, 25f).apply {
                    duration = 600 // czas jednego przejścia (w ms)
                    repeatMode = ObjectAnimator.REVERSE
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                animator.start()
            }
        }
    )
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000) // Czas trwania splash screenu
        navController.navigate("frontPage") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        SplashAnimation()
    }
}
