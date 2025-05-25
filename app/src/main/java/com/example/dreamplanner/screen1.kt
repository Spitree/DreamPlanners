package com.example.dreamplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.Models.*
import com.example.dreamplanner.ui.theme.accent

@Composable
fun Screen1(navController: NavController, viewModel: MainViewModel, plans: List<Plan>) {
    val showSleepPanel = remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(16.dp)) {


        Spacer(Modifier.height(8.dp))

        Text("Dzisiejsze plany", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        viewModel.plans.take(3).forEach { plan ->
            var checked by remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .background(accent)
            ) {
                Text("${plan.name} ${plan.place}", modifier = Modifier.weight(1f))
                Text("⏰", fontSize = 12.sp)
                Checkbox(checked = checked, onCheckedChange = { checked = it })
            }
        }

        Text("Twoje cele", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        viewModel.goals.forEach { goal ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .background(accent)
            ) {
                Text(goal.name, modifier = Modifier.weight(1f))
                Checkbox(checked = goal.completed, onCheckedChange = { goal.completed = it })
            }
        }

        Text("Dzienne zajęcia", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        viewModel.dailyTasks.forEach { task ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .background(accent)
            ) {
                Text(task.name, modifier = Modifier.weight(1f))
                Checkbox(checked = task.completed, onCheckedChange = { task.completed = it })
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { showSleepPanel.value = !showSleepPanel.value }) {
            Text(if (showSleepPanel.value) "Ukryj sen" else "Pokaż sen",color = Color.Black)
        }

        if (showSleepPanel.value) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)) {
                Text("Twój sen: 80%", fontWeight = FontWeight.Bold,color = Color.Black,)
                Text("Ostatnie dni:",color = Color.Black)
                viewModel.sleepEntries.forEach {
                    Text("${it.start} - ${it.end}",color = Color.Black)
                }
            }
        }
    }
}
