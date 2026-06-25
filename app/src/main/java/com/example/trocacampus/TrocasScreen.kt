package com.example.trocacampus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class PropostaTroca(
    val nomeInteressado: String,
    val cursoInteressado: String,
    val tempoAtras: String,
    val itemOferecidoPorMim: String,
    val itemRecebido: String,
    val mensagem: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrocasScreen(navController: NavController) {
    val propostasMock = listOf(
        PropostaTroca(
            nomeInteressado = "Carlos Mendes",
            cursoInteressado = "Matemática",
            tempoAtras = "há 2 horas",
            itemOferecidoPorMim = "Arduino Uno + Sensores",
            itemRecebido = "Livro Clean Code",
            mensagem = "\"Oi! Estou interessado no seu Arduino. Tenho o Clean Code em ótimo estado, posso adicionar também alguns LEDs se quiser.\""
        ),
        PropostaTroca(
            nomeInteressado = "Julia Santos",
            cursoInteressado = "Química",
            tempoAtras = "há 1 dia",
            itemOferecidoPorMim = "Calculadora HP 50g",
            itemRecebido = "Química Orgânica - McMurry",
            mensagem = "\"Preciso muito de uma calculadora para a próxima prova. Meu livro está bem conservado!\""
        )
    )

    Scaffold(
        bottomBar = { AppBottomNavigation(navController, currentRoute = "trocas") }
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
                    Icon(Icons.Default.Sync, contentDescription = "Trocas", tint = Color(0xFF4C3EEB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Minhas Trocas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Notifications, contentDescription = "Notificações")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Pendentes (2)") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE0E0FF))
                )
                FilterChip(selected = false, onClick = {}, label = { Text("Aceitas") })
                FilterChip(selected = false, onClick = {}, label = { Text("Concluídas") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(propostasMock) { proposta ->
                    PropostaCard(proposta)
                }
            }
        }
    }
}

@Composable
fun PropostaCard(proposta: PropostaTroca) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(proposta.nomeInteressado.first().toString(), color = Color(0xFF4C3EEB), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(proposta.nomeInteressado, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(proposta.cursoInteressado, fontSize = 12.sp, color = Color.Gray)
                        Text(proposta.tempoAtras, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Aguardando", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Você oferece:", fontSize = 12.sp, color = Color.Gray)
                    Text(proposta.itemOferecidoPorMim, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Recebe:", fontSize = 12.sp, color = Color.Gray)
                    Text(proposta.itemRecebido, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F4FF), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(proposta.mensagem, fontSize = 14.sp, color = Color.DarkGray, fontStyle = FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1B1F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aceitar", fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recusar", fontSize = 14.sp)
                }
            }
        }
    }
}