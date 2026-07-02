package com.example.trocacampus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(navController: NavController, productId: String) {
    val context = LocalContext.current
    var product by remember { mutableStateOf<ProductResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    var currentUserId by remember { mutableStateOf<String?>(null) }

    var showTradeDialog by remember { mutableStateOf(false) }
    var isTrading by remember { mutableStateOf(false) }
    var myProducts by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var selectedOfferedProduct by remember { mutableStateOf<ProductResponse?>(null) }
    var userToken by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        try {
            userToken = sessionManager.fetchAuthToken()

            if (userToken != null) {
                val meResponse = ApiClient.authApi.getMe("Bearer $userToken")
                if (meResponse.isSuccessful) currentUserId = meResponse.body()?.id

                val myProdsResponse = ApiClient.authApi.getMyProducts("Bearer $userToken")
                if (myProdsResponse.isSuccessful) {
                    myProducts = myProdsResponse.body()?.filter { it.status == "ACTIVE" } ?: emptyList()
                }
            }

            val response = ApiClient.authApi.getProductById(productId)
            if (response.isSuccessful) {
                product = response.body()
            } else {
                Toast.makeText(context, "Erro ao carregar produto.", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Anúncio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!isLoading && product != null) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
                    PaddingValues(16.dp)

                    if (product!!.userId == currentUserId) {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        ) {
                            Text("Este é o seu anúncio", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (userToken == null) {
                                    Toast.makeText(context, "Faça login novamente.", Toast.LENGTH_SHORT).show()
                                } else {
                                    showTradeDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C3EEB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Propor Troca", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF4C3EEB))
            } else if (product != null) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    // --- AQUI ESTÁ A MUDANÇA DA IMAGEM DO BANNER ---
                    val imageUrl = product!!.photos?.firstOrNull()?.url?.replace("http://", "https://")
                    if (!imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Imagem de ${product!!.title}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sem Imagem", color = Color.Gray, fontSize = 18.sp)
                        }
                    }
                    // -----------------------------------------------

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(product!!.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = Color(0xFF4C3EEB), contentColor = Color.White) {
                                Text(product!!.category?.name ?: "Sem Categoria", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            val conditionText = when(product!!.condition) {
                                "NEW" -> "Novo"
                                "GOOD" -> "Seminovo"
                                else -> "Usado"
                            }
                            Badge(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black) {
                                Text(conditionText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        if (!product!!.interests.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Interesse de Troca", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(product!!.interests!!, fontSize = 16.sp, color = Color(0xFF4C3EEB), fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Descrição do Item", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(product!!.description, fontSize = 16.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Anunciado por", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Dono", modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(product!!.user?.name ?: "Usuário", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(product!!.user?.campus ?: "Campus não informado", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }
        }

        if (showTradeDialog) {
            AlertDialog(
                onDismissRequest = {
                    showTradeDialog = false
                    selectedOfferedProduct = null
                },
                title = { Text("O que você vai oferecer?", fontWeight = FontWeight.Bold) },
                text = {
                    if (myProducts.isEmpty()) {
                        Text("Você não tem nenhum anúncio ativo para oferecer em troca. Crie um anúncio primeiro!", color = Color.Gray)
                    } else {
                        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                            myProducts.forEach { meuProduto ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedOfferedProduct = meuProduto }
                                        .background(if (selectedOfferedProduct == meuProduto) Color(0xFFE0E0FF) else Color.Transparent)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedOfferedProduct == meuProduto,
                                        onClick = { selectedOfferedProduct = meuProduto }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(meuProduto.title, fontWeight = if (selectedOfferedProduct == meuProduto) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (myProducts.isNotEmpty()) {
                        Button(
                            onClick = {
                                if (selectedOfferedProduct == null) {
                                    Toast.makeText(context, "Selecione um item!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isTrading = true
                                showTradeDialog = false

                                scope.launch {
                                    try {
                                        val request = TradeRequest(
                                            productId = product!!.id,
                                            offeredProductId = selectedOfferedProduct!!.id
                                        )
                                        val response = ApiClient.authApi.createTrade("Bearer $userToken", request)

                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Proposta enviada!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Erro ao enviar proposta. Talvez já exista uma pendente.", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erro de conexão.", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isTrading = false
                                    }
                                }
                            },
                            enabled = selectedOfferedProduct != null && !isTrading
                        ) {
                            Text("Confirmar")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTradeDialog = false
                        selectedOfferedProduct = null
                    }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}