package com.example.dreamplanner

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.database.PlanViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen4(navController: NavController, viewModel: PlanViewModel) {
    val context = LocalContext.current
    // Obserwuj LiveData jako State
    val plans by viewModel.plans.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        Text("Your Plans")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(plans) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Działanie po kliknięciu (opcjonalnie)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = plan.name, fontSize = 20.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ShareButton(context)
            Spacer(modifier = Modifier.height(8.dp))
            SmsButton()
        }
    }
}

