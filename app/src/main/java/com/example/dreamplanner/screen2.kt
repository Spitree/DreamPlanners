package com.example.dreamplanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.Models.*
import com.example.dreamplanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen2(navController: NavController, viewModel: MainViewModel, sleeps: List<Plan>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Sleep") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Pierwsza lista kart
            Text("Sleep Entries", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(sleeps) { sleep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { /* szczegóły snu */ },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = sleep.name, fontSize = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Obrazek lub wykres
            Text("Sleep Chart", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.placeholder), // ← dodaj swój wykres jako PNG do drawable
                contentDescription = "Sleep chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Druga lista (np. podsumowania snu)
            Text("Summary", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                items(sleeps.take(3)) { sleep ->
                    Text(
                        text = "- ${sleep.name}",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Main Screen")
            }
        }
    }
}
