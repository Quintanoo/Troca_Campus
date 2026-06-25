package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val PurpleBackground = Color(0xFF4C3EEB)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Controlador para esconder o teclado
    val keyboardController = LocalSoftwareKeyboardController.current

    // Variáveis para a requisição da API
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

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
                .weight(2.2f),
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
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("aluno@teste.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true, // Trava em uma linha
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next // Mostra botão de "Próximo"
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Senha", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("123") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true, // Trava em uma linha
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done // Mostra botão de "Confirmar" (check)
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() } // Esconde o teclado ao confirmar
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboardController?.hide() // Esconde o teclado ao clicar no botão

                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Tratamento do texto para evitar erros do teclado do Android
                        val cleanEmail = email.trim().lowercase()

                        // --- BACKUP / LOGIN MESTRE ---
                        if (cleanEmail == "aluno@teste.com" && password == "123") {
                            Toast.makeText(context, "Login Mestre ativado!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                            return@Button // Para a execução aqui e não chama a API
                        }

                        // --- FLUXO REAL DA API ---
                        isLoading = true

                        scope.launch {
                            try {
                                val request = LoginRequest(email = cleanEmail, password = password)
                                val response = ApiClient.authApi.login(request)

                                if (response.isSuccessful) {
                                    val loginData = response.body()
                                    loginData?.token?.let { sessionManager.saveAuthToken(it) }
                                    Toast.makeText(context, "Bem-vindo, ${loginData?.user?.name}!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao conectar com o servidor.", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleBackground),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading // Desativa o botão enquanto carrega
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar ->", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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