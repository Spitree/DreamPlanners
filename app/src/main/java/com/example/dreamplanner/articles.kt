package com.example.dreamplanner

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamplanner.database.Article
import com.example.dreamplanner.database.Plan
import com.example.dreamplanner.database.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Articles(navController: NavController, viewModel: PlanViewModel,isLoggedIn: Boolean) {
    val isLoggedIn = viewModel.isLoggedIn
    if (!isLoggedIn) {
        NotLoggedInScreen(navController)
        return
    }
    val articles by viewModel.article.observeAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text("Dream Archives")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                ExpandableSection("Lucid Dreams", articles.filter { it.section == "Lucid Dreams" })
            }
            item {
                ExpandableSection("Nightmares", articles.filter { it.section == "Nightmares" })
            }
            item {
                ExpandableSection("Recurring Dreams", articles.filter { it.section == "Recurring Dreams" })
            }
            item {
                ExpandableSection("Other Dreams", articles.filter { it.section == "Other Dreams" })
            }
        }
    }
}

@Composable
fun ExpandableSection(title: String, articles: List<Article>) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                articles.forEach { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                context.startActivity(intent)
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = article.name, fontSize = 16.sp)
                            article.description?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotLoggedInScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Zaloguj się, aby uzyskać dostęp do artykułów", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            navController.navigate("login")
        }) {
            Text("Zaloguj się")
        }
    }
}
