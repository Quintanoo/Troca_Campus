package com.example.trocacampus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Estrutura Mockada para a Apresentação
data class MockNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: String // "TRADE_REQUEST" ou "TRADE_ACCEPTED"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    // Lista fixa simulando o banco de dados
    val notifications = listOf(
        MockNotification(
            id = "1",
            title = "Nova proposta de troca!",
            message = "Zezinho ofereceu 'Livro de Física' pelo seu 'bolo teste'.",
            time = "Agora mesmo",
            isRead = false,
            type = "TRADE_REQUEST"
        ),
        MockNotification(
            id = "2",
            title = "Troca aceita!",
            message = "Ana Silva aceitou sua proposta para o 'Cálculo Volume 1'. Entre em contato para combinarem a troca no campus.",
            time = "Há 2 horas",
            isRead = true,
            type = "TRADE_ACCEPTED"
        ),
        MockNotification(
            id = "3",
            title = "Dica do TrocaCampus",
            message = "Adicione mais fotos aos seus anúncios para conseguir trocas mais rápidas!",
            time = "Ontem",
            isRead = true,
            type = "INFO"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificações", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notif ->
                    NotificationCard(notif)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: MockNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF4F3FF)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (notification.type) {
                "TRADE_REQUEST" -> Icons.Default.Sync
                "TRADE_ACCEPTED" -> Icons.Default.CheckCircle
                else -> Icons.Default.Notifications
            }
            val iconColor = when (notification.type) {
                "TRADE_REQUEST" -> Color(0xFF4C3EEB)
                "TRADE_ACCEPTED" -> Color(0xFF00C853)
                else -> Color.Gray
            }
            val bgColor = when (notification.type) {
                "TRADE_REQUEST" -> Color(0xFFE0E0FF)
                "TRADE_ACCEPTED" -> Color(0xFFE8F5E9)
                else -> Color.LightGray
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4C3EEB))
                )
            }
        }
    }
}