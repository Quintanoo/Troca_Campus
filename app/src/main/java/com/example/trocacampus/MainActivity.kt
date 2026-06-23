package com.example.trocacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trocacampus.ui.theme.TrocaCampusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrocaCampusTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { // <--- Ação de ir para o cadastro
                    navController.navigate("register")
                }
            )
        }

        // NOVA ROTA: Tela de Cadastro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Se o cadastro der certo, já joga o usuário para a Home
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    // Botão de voltar para a tela de login
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("anunciar") {
            AnunciarScreen(navController = navController)
        }
        composable("trocas") {
            TrocasScreen(navController = navController)
        }
    }
}