package com.example.trocacampus

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Produto(
    val titulo: String,
    val categoria: String,
    val estado: String,
    val anunciante: String,
    val curso: String,
    val trocas: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val mockProdutos = listOf(
        Produto("Cálculo Volume 1 - James Stewart", "Livros", "Bom", "Ana Silva", "Engenharia Civil", listOf("Física 1", "Álgebra")),
        Produto("Kit Arduino Uno R3", "Componentes", "Novo", "Marcos Lima", "Engenharia Elétrica", listOf("Raspberry Pi", "Multímetro")),
        Produto("Calculadora Científica Casio", "Eletrônicos", "Ótimo", "Carlos Mendes", "Matemática", listOf("Livros de TI", "Caderno")),
        Produto("Jaleco Branco Tamanho M", "Materiais", "Usado", "Julia Santos", "Química", listOf("Óculos de Proteção"))
    )

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
                shape = RoundedCornerShape(12.dp)
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
                    FilterChip(selected = false, onClick = {}, label = { Text("Componentes") })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(mockProdutos) { produto ->
                    ProductCard(produto)
                }
            }
        }
    }
}

@Composable
fun ProductCard(produto: Produto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(produto.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Estado: ${produto.estado}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0FF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(produto.anunciante.first().toString(), color = Color(0xFF4C3EEB), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(produto.anunciante, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(produto.curso, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Troca por: ", fontSize = 12.sp, color = Color.Gray)
                produto.trocas.forEach { item ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(item, fontSize = 10.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
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