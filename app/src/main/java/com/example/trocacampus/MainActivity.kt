package com.example.trocacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trocacampus.ui.theme.TrocaCampusTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

    // 1. Pega o contexto e inicializa o gerenciador de sessão
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // 2. Busca o token na memória do telemóvel
    val tokenSalvo = sessionManager.fetchAuthToken()

    // 3. Decide dinamicamente qual será o primeiro ecrã
    val telaInicial = if (tokenSalvo != null) {
        "home" // Se tem token, pula direto para a Home
    } else {
        "login" // Se não tem (ou fez logout), vai para o Login
    }

    // 4. Passa o ecrã escolhido para o startDestination
    NavHost(navController = navController, startDestination = telaInicial) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
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
        composable(
            route = "product_details/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(navController = navController, productId = productId)
        }
    }
}