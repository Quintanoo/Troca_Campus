package com.example.trocacampus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { AppBottomNavigation(navController, currentRoute = "profile") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
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
                Icon(Icons.Default.Settings, contentDescription = "Configurações")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4C3EEB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("JP", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("João Pedro Santos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("joao.santos@universidade.edu.br", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Eng. da Computação - 6º Sem", fontSize = 12.sp, color = Color(0xFF4C3EEB))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(valor = "12", label = "Trocas")
                        ProfileStat(valor = "4.8", label = "Avaliação")
                        ProfileStat(valor = "5", label = "Anúncios")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Meus Anúncios Ativos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Estruturas de Dados em C", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Livros", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Status: Ativo", fontSize = 12.sp, color = Color(0xFF00C853), fontWeight = FontWeight.SemiBold)
                    }
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