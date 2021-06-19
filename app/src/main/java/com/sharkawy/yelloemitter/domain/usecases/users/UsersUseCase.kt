package com.sharkawy.yelloemitter.domain.usecases.users

import com.sharkawy.yelloemitter.entities.users.UsersResponse
import kotlinx.coroutines.Deferred

val usersUseCase:UsersUseCase by lazy { UsersUseCaseImpl() }
interface UsersUseCase {
    suspend fun getUsers(): UsersResponse
}