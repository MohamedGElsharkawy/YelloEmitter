package com.sharkawy.yelloemitter.domain.repositories.users

import com.sharkawy.yelloemitter.domain.gateways.server.UsersApi
import com.sharkawy.yelloemitter.domain.gateways.server.usersApi
import com.sharkawy.yelloemitter.entities.users.UsersResponse
import kotlinx.coroutines.Deferred

class UsersRepositoryImpl(private val server: UsersApi = usersApi) : UsersRepository {
    override suspend fun getUsers(): UsersResponse =
        server.getUsers()
}