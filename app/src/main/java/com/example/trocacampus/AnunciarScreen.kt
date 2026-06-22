package com.example.trocacampus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunciarScreen(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var itensInteresse by remember { mutableStateOf("") }

    // --- NOVOS ESTADOS PARA OS DROPDOWNS E LISTA ---
    // Estados para Categoria
    val categorias = listOf("Livros", "Eletrônicos", "Componentes", "Materiais", "Outros")
    var expandedCategoria by remember { mutableStateOf(false) }
    var selectedCategoria by remember { mutableStateOf("") }

    // Estados para Estado de Conservação
    val estadosConservacao = listOf("Novo", "Ótimo", "Bom", "Usado", "Com defeito")
    var expandedEstado by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf("") }

    // Estado para a lista de interesses adicionados no botão "+"
    var listaInteresses by remember { mutableStateOf(listOf<String>()) }
    // ------------------------------------------------

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload", tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Toque para adicionar fotos", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Título *", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text("Ex: Cálculo Volume 1 - James Stewart") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- DROPDOWNS FUNCIONANDO ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Dropdown de Categoria
                Column(modifier = Modifier.weight(1f)) {
                    Text("Categoria *", fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        OutlinedTextField(
                            value = selectedCategoria,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecione") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            categorias.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        selectedCategoria = opcao
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown de Estado
                Column(modifier = Modifier.weight(1f)) {
                    Text("Estado *", fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = expandedEstado,
                        onExpandedChange = { expandedEstado = !expandedEstado }
                    ) {
                        OutlinedTextField(
                            value = selectedEstado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecione") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEstado,
                            onDismissRequest = { expandedEstado = false }
                        ) {
                            estadosConservacao.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        selectedEstado = opcao
                                        expandedEstado = false
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
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÃO DE MAIS FUNCIONANDO ---
            Text("Itens de Interesse para Troca", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = itensInteresse,
                onValueChange = { itensInteresse = it },
                placeholder = { Text("Ex: Física 1, Calculadora") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    // Transformamos o Icon simples num IconButton clicável
                    IconButton(onClick = {
                        if (itensInteresse.isNotBlank()) {
                            listaInteresses = listaInteresses + itensInteresse.trim()
                            itensInteresse = "" // Limpa o campo de texto
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                    }
                }
            )

            // Exibe os chips dos itens adicionados logo abaixo
            if (listaInteresses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaInteresses) { item ->
                        AssistChip(
                            onClick = {
                                // Remove o item se o usuário clicar nele
                                listaInteresses = listaInteresses - item
                            },
                            label = { Text(item) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remover", modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C3EEB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publicar Anúncio", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}