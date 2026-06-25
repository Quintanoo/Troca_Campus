package com.example.trocacampus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response

// 1. Classes de Dados (O que enviamos e o que recebemos do Back-end)
data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val campus: String,
    val phone: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val campus: String,
    val phone: String?,
    val reputation: Double,
    val status: String
)

data class LoginResponse(val user: User, val token: String)

// --- NOVAS CLASSES PARA PRODUTOS ---
data class ProductRequest(
    val title: String,
    val description: String,
    val categoryId: String,
    val condition: String
)

data class ProductResponse(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val condition: String,
    val userId: String
)

// 2. A Interface com as rotas
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<User>

    // NOVA ROTA PARA CRIAR ANÚNCIO:
    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body request: ProductRequest
    ): Response<ProductResponse>
}

// 3. O Cliente Retrofit
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3333/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}