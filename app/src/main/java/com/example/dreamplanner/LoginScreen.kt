package com.example.dreamplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamplanner.database.*
import com.example.dreamplanner.ui.theme.DreamPlannerTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController,viewModel: PlanViewModel, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Logowanie", fontSize = 28.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(32.dp))

        androidx.compose.material3.OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Login") },
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
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
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
        Spacer(modifier = Modifier.height(32.dp))

        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        Button(onClick = {
            scope.launch {
                val isAuthenticated = viewModel.authenticateUser(username, password)
                if (isAuthenticated) {
                    viewModel.loginUser(username)  // ustawiamy username
                    onLoginSuccess()
                    navController.navigate("frontPage") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    val userExists = viewModel.userExists(username)
                    if (!userExists) {
                        Toast.makeText(context, "Niepoprawna nazwa użytkownika", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Niepoprawne hasło", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            Text("Zaloguj się", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
        ) {
            Text("Back to Main Menu", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nie masz konta? Zarejestruj się",
            color = Color.Black,
            modifier = Modifier.clickable {
                navController.navigate("register")
            }
        )
    }
}

@Composable
fun RegisterScreen(navController: NavController, viewModel: PlanViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // <- PRZENIESIONE TUTAJ

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Rejestracja", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = { username = it },            colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.Gray,
            focusedTextColor = Color.Gray,
            unfocusedTextColor = Color.Gray
        ), label = { Text("Login") })
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it },
                colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.Gray,
            focusedTextColor = Color.Gray,
            unfocusedTextColor = Color.Gray
        )
            , label = { Text("Hasło") })

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            scope.launch {
                val success = viewModel.registerUser(username, password)
                if (success) {
                    Toast.makeText(context, "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Zarejestruj się",color = Color.Black)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
        ) {
            Text("Back to Main Menu", color = Color.Black)
        }

    }
}


