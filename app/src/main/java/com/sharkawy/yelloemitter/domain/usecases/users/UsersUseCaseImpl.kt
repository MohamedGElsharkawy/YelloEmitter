package com.sharkawy.yelloemitter.domain.usecases.users

import com.sharkawy.yelloemitter.domain.repositories.users.UsersRepository
import com.sharkawy.yelloemitter.domain.repositories.users.usersRepository
import com.sharkawy.yelloemitter.entities.users.UsersResponse
import kotlinx.coroutines.Deferred

class UsersUseCaseImpl(private val repository: UsersRepository = usersRepository) : UsersUseCase {
    override suspend fun getUsers(): UsersResponse =
        repository.getUsers()

}