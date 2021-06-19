package com.sharkawy.yelloemitter.domain.repositories.users

import com.sharkawy.yelloemitter.entities.users.UsersResponse
import kotlinx.coroutines.Deferred

val usersRepository: UsersRepository by lazy { UsersRepositoryImpl() }


interface UsersRepository {
    suspend fun getUsers(): UsersResponse
}