package com.sharkawy.yelloemitter.presentation.features.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sharkawy.yelloemitter.domain.usecases.users.UsersUseCase
import com.sharkawy.yelloemitter.domain.usecases.users.usersUseCase
import com.sharkawy.yelloemitter.entities.Resource
import kotlinx.coroutines.Dispatchers

class UsersViewModel(private val useCase: UsersUseCase=usersUseCase):ViewModel() {

    fun getUsers() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = useCase.getUsers()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }

    }
}