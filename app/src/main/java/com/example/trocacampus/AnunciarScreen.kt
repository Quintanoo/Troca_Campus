package com.example.trocacampus

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// Função auxiliar para transformar a Uri da galeria num File real
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_img", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunciarScreen(navController: NavController) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    val sessionManager = remember { SessionManager(context) }

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var interesse by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    val categorias = listOf("Livros", "Eletrônicos", "Materiais", "Outros")
    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSelecionado by remember { mutableStateOf("") }
    val estadosItem = listOf("Novo", "Seminovo", "Usado")
    var isLoading by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
            }
        }
    )

    Scaffold(
        bottomBar = { AppBottomNavigation(navController, currentRoute = "anunciar") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, contentDescription = "Criar", tint = Color(0xFF4C3EEB))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar Anúncio", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Fotos do Item", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (selectedImageUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Upload", tint = Color.Gray)
                        Text("Toque para adicionar foto", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Foto selecionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Toque para trocar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Título *", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text("Ex: Cálculo Volume 1 - James Stewart") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Categoria *", fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = categoriaExpanded,
                        onExpandedChange = { categoriaExpanded = !categoriaExpanded }
                    ) {
                        OutlinedTextField(
                            value = categoriaSelecionada.ifEmpty { "Selecione" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = categoriaExpanded,
                            onDismissRequest = { categoriaExpanded = false }
                        ) {
                            categorias.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        categoriaSelecionada = selectionOption
                                        categoriaExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Estado *", fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = estadoExpanded,
                        onExpandedChange = { estadoExpanded = !estadoExpanded }
                    ) {
                        OutlinedTextField(
                            value = estadoSelecionado.ifEmpty { "Selecione" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = estadoExpanded,
                            onDismissRequest = { estadoExpanded = false }
                        ) {
                            estadosItem.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        estadoSelecionado = selectionOption
                                        estadoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Descrição *", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Descreva as condições, anotações, marcas de uso...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Itens de Interesse para Troca", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = interesse,
                onValueChange = { interesse = it },
                placeholder = { Text("Ex: Física 1, Calculadora") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                trailingIcon = {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Interesse")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()

                    if (titulo.isBlank() || categoriaSelecionada.isBlank() || estadoSelecionado.isBlank() || descricao.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val token = sessionManager.fetchAuthToken()
                    if (token == null) {
                        Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        return@Button
                    }

                    val backendCategoryId = when (categoriaSelecionada) {
                        "Livros" -> "fb540823-987b-4f05-a8c5-360474b46671"
                        "Eletrônicos" -> "36821c83-22f1-4217-aab5-6a822c05e2c1"
                        "Materiais" -> "277ad896-fe3b-409a-9612-d7a99a03c6b3"
                        else -> "2b5f14a4-4cfc-4a4f-836c-9b3538fe9f66"
                    }

                    val backendCondition = when (estadoSelecionado) {
                        "Novo" -> "NEW"
                        "Seminovo" -> "GOOD"
                        "Usado" -> "USED"
                        else -> "USED"
                    }

                    isLoading = true

                    scope.launch {
                        try {
                            // Converte textos para RequestBody
                            val titleBody = titulo.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                            val descBody = descricao.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                            val catBody = backendCategoryId.toRequestBody("text/plain".toMediaTypeOrNull())
                            val condBody = backendCondition.toRequestBody("text/plain".toMediaTypeOrNull())
                            val intBody = interesse.trim().takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

                            // Prepara a foto
                            var photoPart: MultipartBody.Part? = null
                            selectedImageUri?.let { uri ->
                                val file = getFileFromUri(context, uri)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                    photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                                }
                            }

                            // Faz a requisição Multipart
                            val response = ApiClient.authApi.createProduct(
                                token = "Bearer $token",
                                title = titleBody,
                                description = descBody,
                                categoryId = catBody,
                                condition = condBody,
                                interests = intBody,
                                photo = photoPart
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Anúncio publicado com sucesso!", Toast.LENGTH_SHORT).show()
                                titulo = ""
                                descricao = ""
                                interesse = ""
                                categoriaSelecionada = ""
                                estadoSelecionado = ""
                                selectedImageUri = null
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Erro ao publicar anúncio.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro de conexão com o servidor.", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C3EEB)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Publicar Anúncio", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}