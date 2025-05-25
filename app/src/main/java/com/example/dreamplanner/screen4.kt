package com.example.dreamplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.Models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen4(navController: NavController, viewModel: MainViewModel, plans: List<Plan>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Plans") }
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
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = plan.name, fontSize = 20.sp)
                        }
                    }
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
