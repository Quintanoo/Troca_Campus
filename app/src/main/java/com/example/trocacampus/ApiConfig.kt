package com.example.trocacampus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response

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

data class Category(val id: String, val name: String)

data class ProductPhoto(val id: String, val url: String)

data class ProductRequest(
    val title: String,
    val description: String,
    val categoryId: String,
    val condition: String,
    val interests: String? // <--- NOVO
)

data class TradeRequest(
    val productId: String,
    val offeredProductId: String // <--- NOVO
)

data class TradeResponse(
    val id: String,
    val requesterId: String,
    val productId: String,
    val status: String
)
data class ProductResponse(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val condition: String,
    val status: String,
    val userId: String,
    val interests: String?, // <--- ESSENCIAL PARA O COMPILADOR NÃO RECLAMAR
    val category: Category?,
    val photos: List<ProductPhoto>?,
    val user: User?
)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<User>

    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @GET("products/my")
    suspend fun getMyProducts(@Header("Authorization") token: String): Response<List<ProductResponse>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductResponse>

    @GET("products")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    @POST("trades")
    suspend fun createTrade(
        @Header("Authorization") token: String,
        @Body request: TradeRequest
    ): Response<TradeResponse>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3333/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}