package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    val PurpleBackground = Color(0xFF4C3EEB)
    val context = LocalContext.current

    // Variáveis do formulário
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        // --- TOPO (Botão de voltar e Título) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackToLogin() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Criar Nova Conta",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- FORMULÁRIO ROLÁVEL (Card Branco) ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Ocupa todo o resto da tela
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Permite rolar a tela com o teclado aberto
            ) {
                // Nome
                Text(text = "Nome Completo", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = nome, onValueChange = { nome = it },
                    placeholder = { Text("Ex: João Pedro Santos") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                Text(text = "Email Institucional", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    placeholder = { Text("aluno@universidade.edu.br") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Curso
                Text(text = "Curso", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = curso, onValueChange = { curso = it },
                    placeholder = { Text("Ex: Engenharia da Computação") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Senha
                Text(text = "Senha", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = senha, onValueChange = { senha = it },
                    placeholder = { Text("Mínimo de 6 caracteres") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirmar Senha
                Text(text = "Confirmar Senha", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = confirmarSenha, onValueChange = { confirmarSenha = it },
                    placeholder = { Text("Repita a senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botão Cadastrar (Mock de validação)
                Button(
                    onClick = {
                        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        } else if (senha != confirmarSenha) {
                            Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess() // Vai direto para a Home
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Finalizar Cadastro", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão de já tenho conta (opcional, faz a mesma coisa que a seta no topo)
                TextButton(
                    onClick = { onBackToLogin() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Já tenho uma conta. Fazer Login", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(32.dp)) // Espaço extra para o final da rolagem
            }
        }
    }
}