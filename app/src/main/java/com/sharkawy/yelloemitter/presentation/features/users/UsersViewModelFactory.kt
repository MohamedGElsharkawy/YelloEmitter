package com.sharkawy.yelloemitter.presentation.features.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharkawy.yelloemitter.domain.usecases.users.UsersUseCase

class UsersViewModelFactory(private val useCase: UsersUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}