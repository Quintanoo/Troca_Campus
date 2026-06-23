package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val PurpleBackground = Color(0xFF4C3EEB)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("TrocaCampus", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Marketplace de trocas acadêmicas seguro e\nexclusivo para sua universidade",
                color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.2f), // Aumentei um pouquinho o espaço branco
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Entrar na conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Email Institucional", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    placeholder = { Text("aluno@teste.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Senha", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    placeholder = { Text("123") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email == "aluno@teste.com" && password == "123") {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entrar ->", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NOVO BOTÃO DE CADASTRO AQUI ---
                TextButton(
                    onClick = { onNavigateToRegister() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Não tem uma conta? Cadastre-se", color = PurpleBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}