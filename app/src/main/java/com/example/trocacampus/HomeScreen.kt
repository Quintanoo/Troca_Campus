package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    val mockProdutos = listOf(
        ProductResponse(
            id = "mock-1",
            title = "Cálculo Volume 1 - James Stewart",
            description = "Livro em ótimo estado",
            categoryId = "mock",
            condition = "GOOD",
            status = "ACTIVE",
            userId = "mock-u1",
            interests = "Física 1, Álgebra",
            category = Category("mock", "Livros"),
            photos = emptyList(),
            user = User("mock-u1", "Ana Silva", "ana@teste.com", "Engenharia Civil", null, 5.0, "ACTIVE")
        ),
        ProductResponse(
            id = "mock-2",
            title = "Kit Arduino Uno R3",
            description = "Quase sem uso",
            categoryId = "mock2",
            condition = "NEW",
            status = "ACTIVE",
            userId = "mock-u2",
            interests = "Raspberry Pi, Multímetro",
            category = Category("mock2", "Componentes"),
            photos = emptyList(),
            user = User("mock-u2", "Marcos Lima", "marcos@teste.com", "Engenharia Elétrica", null, 5.0, "ACTIVE")
        )
    )

    var products by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.authApi.getAllProducts()
            products = if (response.isSuccessful) {
                (response.body() ?: emptyList()) + mockProdutos
            } else {
                mockProdutos
            }
        } catch (e: Exception) {
            products = mockProdutos
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = { AppBottomNavigation(navController, currentRoute = "home") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = "Logo", tint = Color(0xFF4C3EEB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TrocaCampus", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Notifications, contentDescription = "Notificações")
            }

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar itens...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(1) {
                    FilterChip(selected = true, onClick = {}, label = { Text("Todos") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Livros") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Eletrônicos") })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4C3EEB))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(products) { produto ->
                        ProductCard(produto, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(produto: ProductResponse, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("product_details/${produto.id}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                // --- AQUI ESTÁ A MUDANÇA DA IMAGEM ---
                val imageUrl = produto.photos?.firstOrNull()?.url?.replace("http://", "https://")

                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagem do Item",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Item", tint = Color.Gray)
                    }
                }
                // ------------------------------------

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(produto.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                    val conditionText = when(produto.condition) {
                        "NEW" -> "Novo"
                        "GOOD" -> "Seminovo"
                        "USED" -> "Usado"
                        else -> "Usado"
                    }
                    Text("Estado: $conditionText", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0FF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val inicial = produto.user?.name?.firstOrNull()?.toString()?.uppercase() ?: "U"
                            Text(inicial, color = Color(0xFF4C3EEB), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(produto.user?.name ?: "Usuário Desconhecido", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(produto.user?.campus ?: "Campus não informado", fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            if (produto.description.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Descrição: ${produto.description}", fontSize = 12.sp, color = Color.DarkGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavController, currentRoute: String) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { if (currentRoute != "home") navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4C3EEB))
        )
        NavigationBarItem(
            selected = currentRoute == "anunciar",
            onClick = { if (currentRoute != "anunciar") navController.navigate("anunciar") },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Anunciar") },
            label = { Text("Anunciar") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4C3EEB))
        )
        NavigationBarItem(
            selected = currentRoute == "trocas",
            onClick = { if (currentRoute != "trocas") navController.navigate("trocas") },
            icon = { Icon(Icons.Default.Sync, contentDescription = "Trocas") },
            label = { Text("Trocas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4C3EEB))
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { if (currentRoute != "profile") navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4C3EEB))
        )
    }
}