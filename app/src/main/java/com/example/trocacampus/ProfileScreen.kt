package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var userProfile by remember { mutableStateOf<User?>(null) }
    var myProducts by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            navController.navigate("login") { popUpTo(0) { inclusive = true } }
            return@LaunchedEffect
        }

        try {
            // Busca o perfil e os produtos ao mesmo tempo
            val profileResponse = ApiClient.authApi.getMe("Bearer $token")
            val productsResponse = ApiClient.authApi.getMyProducts("Bearer $token")

            if (profileResponse.isSuccessful) {
                userProfile = profileResponse.body()
            }
            if (productsResponse.isSuccessful) {
                myProducts = productsResponse.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro de conexão com o servidor.", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = { AppBottomNavigation(navController, currentRoute = "profile") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF4C3EEB))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Cabeçalho e Logout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF4C3EEB))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Meu Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = {
                            sessionManager.clearSession()
                            Toast.makeText(context, "Até logo!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = Color.Red)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Card do Usuário
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val iniciais = userProfile?.name?.take(2)?.uppercase() ?: "U"
                                Box(
                                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF4C3EEB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(iniciais, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(userProfile?.name ?: "Usuário", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(userProfile?.email ?: "", fontSize = 14.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(userProfile?.campus ?: "Não informado", fontSize = 12.sp, color = Color(0xFF4C3EEB))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileStat(valor = "0", label = "Trocas")
                                ProfileStat(valor = userProfile?.reputation?.toString() ?: "5.0", label = "Avaliação")
                                ProfileStat(valor = myProducts.size.toString(), label = "Anúncios")
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = {
                                    sessionManager.clearSession()
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4C3EEB))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Trocar", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Trocar de conta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Meus Anúncios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista Dinâmica de Anúncios
                    if (myProducts.isEmpty()) {
                        Text("Você ainda não tem anúncios publicados.", color = Color.Gray)
                    } else {
                        myProducts.forEach { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { navController.navigate("product_details/${product.id}") },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = "Item", tint = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(product.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(product.category?.name ?: "Sem Categoria", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))

                                        val statusBr = if (product.status == "ACTIVE") "Ativo" else "Inativo"
                                        val statusColor = if (product.status == "ACTIVE") Color(0xFF00C853) else Color.Red

                                        Text("Status: $statusBr", fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileStat(valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4C3EEB))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}