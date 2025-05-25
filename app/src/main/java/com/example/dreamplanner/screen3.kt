package com.example.dreamplanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.Models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3(navController: NavController, viewModel: MainViewModel, articles: List<Plan>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dream Archives") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ExpandableSection("Lucid Dreams", articles)
            ExpandableSection("Nightmares", articles)
            ExpandableSection("Recurring Dreams", articles)
            ExpandableSection("Other Dreams", articles)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Main Screen")
            }
        }
    }
}

@Composable
fun ExpandableSection(title: String, items: List<Plan>) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 20.sp)
        Text(
            text = if (expanded) "-" else "+",
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
    }

    AnimatedVisibility(visible = expanded) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { /* możesz dodać akcję */ },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
