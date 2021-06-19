package com.sharkawy.yelloemitter.domain.gateways.server

import com.sharkawy.yelloemitter.entities.users.UsersResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET


val usersApi: UsersApi by lazy {
    retrofitClient.create(UsersApi::class.java)
}

interface UsersApi {
    @GET(USERS_URL_PATH)
    suspend fun getUsers(
    ): UsersResponse
}