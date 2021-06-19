package com.sharkawy.yelloemitter.presentation.features.users

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sharkawy.yelloemitter.databinding.ActivityMainBinding
import com.sharkawy.yelloemitter.entities.Status

class UsersActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onStart() {
        super.onStart()
        setupAdapter()
        setupViewModel()
        setupObservers()
        fireUsersRefreshing()
    }

    private fun fireUsersRefreshing() {
        binding?.swipeRefresh?.setOnRefreshListener {
            setupObservers()
        }
    }

    private fun setupAdapter() {
        binding?.usersRv?.adapter =
            UsersAdapter({
            }, mutableListOf())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        binding?.swipeRefresh?.isRefreshing = true
                        Log.w("STATUS", "LOADING")
                    }
                    Status.SUCCESS -> {
                        binding?.swipeRefresh?.isRefreshing = false
                        resource.data?.let { users ->
                            binding?.usersRv?.adapter = UsersAdapter({}, users.toMutableList())

                        }
                    }
                    Status.ERROR -> {
                        binding?.swipeRefresh?.isRefreshing = false
                        Log.w("STATUS", "ERROR")
                        Log.w("STATUS", it.message.toString())
                    }

                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}