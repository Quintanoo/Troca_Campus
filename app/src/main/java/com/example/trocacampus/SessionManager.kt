package com.example.trocacampus

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // Cria um arquivo invisível no celular chamado "TrocaCampusPrefs"
    private var prefs: SharedPreferences = context.getSharedPreferences("TrocaCampusPrefs", Context.MODE_PRIVATE)

    // Função para salvar o token do usuário
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString("USER_TOKEN", token)
        editor.apply()
    }

    // Função para resgatar o token quando abrir o app
    fun fetchAuthToken(): String? {
        return prefs.getString("USER_TOKEN", null)
    }

    // Função para o botão de "Sair" (Logout)
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}