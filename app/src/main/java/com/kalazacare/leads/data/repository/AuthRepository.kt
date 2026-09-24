package com.kalazacare.leads.data.repository

interface AuthRepository {
    suspend fun login(adminName: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUserId(): String?
}
